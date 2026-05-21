package com.jarvis.assistant.llm

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteOrder
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GGUFLoader @Inject constructor() {

    data class GGUFMetadata(
        val architecture: String = "",
        val quantization: String = "",
        val contextLength: Int = 2048,
        val parameterCount: Long = 0,
        val ramEstimateMb: Int = 0
    )

    companion object {
        private val GGUF_MAGIC = byteArrayOf(0x47, 0x47, 0x55, 0x46) // "GGUF"
        private val GGML_MAGIC = byteArrayOf(0x47, 0x47, 0x4D, 0x4C) // "GGML" (legacy)
    }

    fun validateMagicBytes(file: File): Boolean {
        return try {
            RandomAccessFile(file, "r").use { raf ->
                val magic = ByteArray(4)
                raf.readFully(magic)
                magic.contentEquals(GGUF_MAGIC) || magic.contentEquals(GGML_MAGIC)
            }
        } catch (_: Exception) {
            false
        }
    }

    fun parseMetadata(file: File): GGUFMetadata {
        if (!validateMagicBytes(file)) {
            return GGUFMetadata()
        }

        return try {
            RandomAccessFile(file, "r").use { raf ->
                val magic = ByteArray(4)
                raf.readFully(magic)

                // Skip past the magic and read version
                val headerBytes = ByteArray(12)
                raf.readFully(headerBytes)
                val headerBuffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN)
                val version = headerBuffer.int
                val tensorCount = headerBuffer.getLong()

                // Read metadata KV pairs count
                val metaCountBytes = ByteArray(8)
                raf.readFully(metaCountBytes)
                val metaCount = ByteBuffer.wrap(metaCountBytes).order(ByteOrder.LITTLE_ENDIAN).getLong()

                var architecture = ""
                var quantization = ""
                var contextLength = 2048
                var parameterCount = 0L

                // Read through metadata to extract key information
                for (i in 0 until minOf(metaCount, 100L)) { // Limit to 100 entries
                    try {
                        val key = readString(raf)
                        val valueType = readInt32(raf)
                        val value = readMetadataValue(raf, valueType)

                        when {
                            key.contains("general.architecture", ignoreCase = true) ->
                                architecture = value
                            key.contains("general.file_type", ignoreCase = true) ||
                                key.contains("general.quantization", ignoreCase = true) ->
                                quantization = value
                            key.contains("llama.context_length", ignoreCase = true) ||
                                key.contains("context_length", ignoreCase = true) ->
                                contextLength = value.toIntOrNull() ?: 2048
                            key.contains("general.parameter_count", ignoreCase = true) ||
                                key.contains("general.size_label", ignoreCase = true) ->
                                parameterCount = value.replace("[^0-9.]".toRegex(), "")
                                    .toDoubleOrNull()?.toLong() ?: 0L
                        }
                    } catch (_: Exception) {
                        break
                    }
                }

                // Estimate RAM from file size and quantization
                val fileSizeMb = (file.length() / (1024 * 1024)).toInt()
                val ramEstimate = when {
                    quantization.contains("4", ignoreCase = true) -> fileSizeMb / 4 * 6
                    quantization.contains("5", ignoreCase = true) -> fileSizeMb / 5 * 7
                    quantization.contains("6", ignoreCase = true) -> fileSizeMb / 6 * 8
                    quantization.contains("8", ignoreCase = true) -> fileSizeMb / 8 * 10
                    else -> fileSizeMb + 1024 // Add 1GB overhead
                }

                GGUFMetadata(
                    architecture = architecture.ifEmpty { guessArchitecture(fileSizeMb) },
                    quantization = quantization.ifEmpty { guessQuantization(fileSizeMb) },
                    contextLength = contextLength,
                    parameterCount = parameterCount,
                    ramEstimateMb = ramEstimate
                )
            }
        } catch (_: Exception) {
            GGUFMetadata()
        }
    }

    private fun readString(raf: RandomAccessFile): String {
        val len = readInt64(raf)
        val bytes = ByteArray(len.toInt())
        raf.readFully(bytes)
        return String(bytes, Charsets.UTF_8)
    }

    private fun readInt32(raf: RandomAccessFile): Int {
        val bytes = ByteArray(4)
        raf.readFully(bytes)
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int
    }

    private fun readInt64(raf: RandomAccessFile): Long {
        val bytes = ByteArray(8)
        raf.readFully(bytes)
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).long
    }

    private fun readMetadataValue(raf: RandomAccessFile, type: Int): String {
        return when (type) {
            0 -> readInt32(raf).toString()     // uint32
            1 -> readInt32(raf).toString()     // int32
            2 -> readInt64(raf).toString()     // float32
            3 -> readInt64(raf).toString()     // bool
            4 -> readString(raf)               // string
            5 -> {                             // array
                val arrayType = readInt32(raf)
                val arrayLen = readInt64(raf)
                val values = mutableListOf<String>()
                for (i in 0 until minOf(arrayLen, 10L)) {
                    values.add(readMetadataValue(raf, arrayType))
                }
                values.joinToString(", ")
            }
            else -> ""
        }
    }

    private fun guessArchitecture(fileSizeMb: Int): String {
        return when {
            fileSizeMb > 10000 -> "Llama 70B"
            fileSizeMb > 5000 -> "Llama 30B"
            fileSizeMb > 3000 -> "Llama 13B"
            fileSizeMb > 1000 -> "Llama 7B"
            fileSizeMb > 500 -> "Mistral 7B"
            else -> "TinyLLaMA"
        }
    }

    private fun guessQuantization(fileSizeMb: Int): String {
        return when {
            fileSizeMb > 10000 -> "Q2_K"
            fileSizeMb > 5000 -> "Q3_K"
            fileSizeMb > 3000 -> "Q4_K_M"
            fileSizeMb > 1000 -> "Q5_K_M"
            else -> "Q4_0"
        }
    }
}

package com.jarvis.assistant.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.assistant.R
import com.jarvis.assistant.data.local.db.entity.ModelMetadataEntity
import com.google.android.material.button.MaterialButton

class ModelCardAdapter(
    private val onSetActive: (ModelMetadataEntity) -> Unit,
    private val onDelete: (ModelMetadataEntity) -> Unit
) : ListAdapter<ModelMetadataEntity, ModelCardAdapter.ModelViewHolder>(ModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_model_card, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvModelName)
        private val tvSize = itemView.findViewById<TextView>(R.id.tvModelSize)
        private val tvArchitecture = itemView.findViewById<TextView>(R.id.tvArchitecture)
        private val tvQuantization = itemView.findViewById<TextView>(R.id.tvQuantization)
        private val tvContext = itemView.findViewById<TextView>(R.id.tvContext)
        private val btnSetActive = itemView.findViewById<MaterialButton>(R.id.btnSetActive)
        private val btnDelete = itemView.findViewById<MaterialButton>(R.id.btnDelete)

        fun bind(model: ModelMetadataEntity) {
            tvName.text = model.fileName
            tvSize.text = formatFileSize(model.fileSize)
            tvArchitecture.text = "Arch: ${model.architecture.ifEmpty { "Unknown" }}"
            tvQuantization.text = "Q: ${model.quantization.ifEmpty { "N/A" }}"
            tvContext.text = "CTX: ${model.contextLength}"

            btnSetActive.text = if (model.isActive) "Active" else "Set Active"
            btnSetActive.isEnabled = !model.isActive

            btnSetActive.setOnClickListener { onSetActive(model) }
            btnDelete.setOnClickListener { onDelete(model) }
        }

        private fun formatFileSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
                else -> "${"%.1f".format(bytes.toDouble() / (1024 * 1024 * 1024))} GB"
            }
        }
    }

    class ModelDiffCallback : DiffUtil.ItemCallback<ModelMetadataEntity>() {
        override fun areItemsTheSame(oldItem: ModelMetadataEntity, newItem: ModelMetadataEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ModelMetadataEntity, newItem: ModelMetadataEntity): Boolean =
            oldItem == newItem
    }
}

package com.jarvis.assistant.presentation.ui.voice

import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentVoiceAssistantBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class VoiceAssistantFragment : Fragment(R.layout.fragment_voice_assistant) {

    @Inject
    lateinit var viewModel: VoiceAssistantViewModel

    private var _binding: FragmentVoiceAssistantBinding? = null
    private val binding get() = _binding!!
    private var speechRecognizer: SpeechRecognizer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVoiceAssistantBinding.bind(view)

        setupToolbar()
        setupVoiceButton()
        observeViewModel()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupVoiceButton() {
        binding.btnVoiceToggle.setOnClickListener {
            if (viewModel.isListening.value) {
                stopListening()
            } else {
                startListening()
            }
        }
    }

    private fun startListening() {
        val intent = android.speech.RecognizerIntent.getIntent(
            android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_now))
        }

        speechRecognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                viewModel.setListening(true)
                binding.tvStatus.text = getString(R.string.listening)
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                viewModel.setListening(false)
                binding.tvStatus.text = getString(R.string.processing)
            }

            override fun onError(error: Int) {
                viewModel.setListening(false)
                binding.tvStatus.text = getString(R.string.tap_to_speak)
            }

            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(
                    android.speech.SpeechRecognizer.RESULTS_RECOGNITION
                )
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    binding.tvTranscript.visibility = View.VISIBLE
                    binding.tvTranscript.text = text
                    viewModel.processVoiceInput(text)
                }
                binding.tvStatus.text = getString(R.string.tap_to_speak)
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {}

            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        viewModel.setListening(false)
        binding.tvStatus.text = getString(R.string.tap_to_speak)
    }

    private fun observeViewModel() {
        viewModel.isListening.observe(viewLifecycleOwner) { listening ->
            binding.btnVoiceToggle.iconTint = if (listening) {
                android.content.res.ColorStateList.valueOf(
                    resources.getColor(android.R.color.black, null)
                )
            } else {
                android.content.res.ColorStateList.valueOf(
                    resources.getColor(android.R.color.black, null)
                )
            }
            binding.btnVoiceToggle.backgroundTintList = if (listening) {
                android.content.res.ColorStateList.valueOf(
                    resources.getColor(android.R.color.holo_red_light, null)
                )
            } else {
                android.content.res.ColorStateList.valueOf(
                    resources.getColor(R.color.neon_blue, null)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        _binding = null
    }
}

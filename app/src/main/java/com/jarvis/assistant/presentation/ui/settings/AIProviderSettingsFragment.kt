package com.jarvis.assistant.presentation.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentAiProviderSettingsBinding
import com.jarvis.assistant.domain.usecase.SwitchProviderUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AIProviderSettingsFragment : Fragment(R.layout.fragment_ai_provider_settings) {

    @Inject
    lateinit var switchProviderUseCase: SwitchProviderUseCase

    @Inject
    lateinit var viewModel: AIProviderViewModel

    private var _binding: FragmentAiProviderSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiProviderSettingsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rgProvider.setOnCheckedChangeListener { _, checkedId ->
            val providerId = when (checkedId) {
                R.id.rbOpenAI -> "openai"
                R.id.rbGemini -> "gemini"
                R.id.rbAgentRouter -> "agentrouter"
                R.id.rbLocal -> "local"
                else -> "openai"
            }
            switchProviderUseCase(providerId)
            loadProviderSettings(providerId)
        }

        binding.btnTestConnection.setOnClickListener {
            testConnection()
        }
    }

    private fun loadProviderSettings(providerId: String) {
        val apiKey = viewModel.getApiKey(providerId)
        val endpoint = viewModel.getEndpoint(providerId)
        binding.etApiKey.setText(apiKey ?: "")
        binding.etEndpoint.setText(endpoint ?: "")
        binding.etEndpoint.visibility = if (providerId == "agentrouter") View.VISIBLE else View.GONE
    }

    private fun testConnection() {
        val providerId = when (binding.rgProvider.checkedRadioButtonId) {
            R.id.rbOpenAI -> "openai"
            R.id.rbGemini -> "gemini"
            R.id.rbAgentRouter -> "agentrouter"
            else -> "openai"
        }
        val apiKey = binding.etApiKey.text.toString()

        lifecycleScope.launch {
            binding.tvTestResult.visibility = View.VISIBLE
            binding.tvTestResult.text = "Testing..."
            val success = viewModel.validateConnection(providerId, apiKey)
            binding.tvTestResult.text = if (success) "Connection successful!" else "Connection failed"
            binding.tvTestResult.setTextColor(
                resources.getColor(if (success) R.color.success_green else R.color.error_red, null)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

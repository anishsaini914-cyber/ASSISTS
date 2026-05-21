package com.jarvis.assistant.presentation.ui.wakeword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jarvis.assistant.R
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import com.jarvis.assistant.databinding.FragmentWakeWordSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WakeWordSettingsFragment : Fragment(R.layout.fragment_wake_word_settings) {

    @Inject
    lateinit var prefs: SecurePreferencesManager

    private var _binding: FragmentWakeWordSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWakeWordSettingsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Load current settings
        binding.switchEnable.isChecked = prefs.isWakeWordEnabled()
        binding.etWakePhrase.setText(prefs.getWakeWord())

        // Save on changes
        binding.switchEnable.setOnCheckedChangeListener { _, isChecked ->
            prefs.setWakeWordEnabled(isChecked)
        }

        binding.etWakePhrase.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                prefs.saveWakeWord(binding.etWakePhrase.text.toString())
            }
        }

        binding.btnTest.setOnClickListener {
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Wake word test: \"${prefs.getWakeWord()}\"",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Save wake phrase on exit
        prefs.saveWakeWord(binding.etWakePhrase.text.toString())
        _binding = null
    }
}

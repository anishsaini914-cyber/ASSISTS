package com.jarvis.assistant.presentation.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private data class SettingItem(
        val title: String,
        val subtitle: String,
        val iconRes: Int,
        val action: () -> Unit
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        val settingsItems = listOf(
            SettingItem(
                getString(R.string.ai_provider),
                "OpenAI, Gemini, Local Models",
                R.drawable.ic_model,
                { findNavController().navigate(R.id.action_settingsFragment_to_aiProviderSettingsFragment) }
            ),
            SettingItem(
                getString(R.string.local_models),
                "Manage imported models",
                R.drawable.ic_cloud_upload,
                { findNavController().navigate(R.id.action_settingsFragment_to_localModelManagerFragment) }
            ),
            SettingItem(
                getString(R.string.wake_word_settings),
                "\"Hey Jarvis\" detection",
                R.drawable.ic_volume,
                { findNavController().navigate(R.id.action_settingsFragment_to_wakeWordSettingsFragment) }
            ),
            SettingItem(
                getString(R.string.permissions),
                "App permissions",
                R.drawable.ic_shield,
                { findNavController().navigate(R.id.action_settingsFragment_to_permissionsFragment) }
            ),
            SettingItem(
                getString(R.string.notifications_title),
                "Recent notifications",
                R.drawable.ic_notifications,
                { findNavController().navigate(R.id.action_settingsFragment_to_notificationsFragment) }
            ),
            SettingItem(
                getString(R.string.about),
                getString(R.string.developer_name),
                R.drawable.ic_info,
                { findNavController().navigate(R.id.action_settingsFragment_to_aboutFragment) }
            )
        )

        binding.rvSettings.adapter = com.jarvis.assistant.presentation.adapter.SettingsAdapter(
            items = settingsItems
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

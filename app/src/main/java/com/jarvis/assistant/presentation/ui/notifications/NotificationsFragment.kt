package com.jarvis.assistant.presentation.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Show empty state for now
        binding.tvEmpty.visibility = View.VISIBLE
        binding.rvNotifications.visibility = View.GONE

        binding.btnAnnounceAll.setOnClickListener {
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "No notifications to announce",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

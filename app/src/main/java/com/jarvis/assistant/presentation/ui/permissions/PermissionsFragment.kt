package com.jarvis.assistant.presentation.ui.permissions

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentPermissionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionsFragment : Fragment(R.layout.fragment_permissions) {

    private var _binding: FragmentPermissionsBinding? = null
    private val binding get() = _binding!!

    private data class PermissionItem(
        val name: String,
        val description: String,
        val permission: String,
        val requestCode: Int
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPermissionsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val permissions = mutableListOf(
            PermissionItem(
                getString(R.string.microphone_permission),
                getString(R.string.microphone_desc),
                Manifest.permission.RECORD_AUDIO,
                1001
            ),
            PermissionItem(
                getString(R.string.phone_permission),
                getString(R.string.phone_desc),
                Manifest.permission.ANSWER_PHONE_CALLS,
                1002
            ),
            PermissionItem(
                getString(R.string.location_permission),
                getString(R.string.location_desc),
                Manifest.permission.ACCESS_COARSE_LOCATION,
                1003
            ),
            PermissionItem(
                getString(R.string.camera_permission),
                getString(R.string.camera_desc),
                Manifest.permission.CAMERA,
                1004
            ),
            PermissionItem(
                getString(R.string.notification_permission),
                getString(R.string.notification_desc),
                Manifest.permission.POST_NOTIFICATIONS,
                1005
            )
        )

        binding.rvPermissions.adapter = PermissionAdapter(permissions) { permission ->
            requestPermissions(arrayOf(permission.permission), permission.requestCode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

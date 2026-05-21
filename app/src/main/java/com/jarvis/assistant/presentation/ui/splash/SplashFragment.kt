package com.jarvis.assistant.presentation.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jarvis.assistant.R
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    @Inject
    lateinit var prefs: SecurePreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1500) // Show splash for 1.5 seconds
            if (prefs.isFirstLaunch()) {
                findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
            }
        }
    }
}

package com.jarvis.assistant.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jarvis.assistant.R
import com.jarvis.assistant.data.local.prefs.SecurePreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    @Inject
    lateinit var prefs: SecurePreferencesManager

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)

        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.ic_jarvis_logo,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
            ),
            OnboardingItem(
                R.drawable.ic_volume,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
            ),
            OnboardingItem(
                R.drawable.ic_model,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
            ),
            OnboardingItem(
                R.drawable.ic_star,
                getString(R.string.onboarding_title_4),
                getString(R.string.onboarding_desc_4)
            )
        )

        viewPager.adapter = OnboardingPagerAdapter(onboardingItems)

        view.findViewById<View>(R.id.btnSkip).setOnClickListener {
            completeOnboarding()
        }

        view.findViewById<View>(R.id.btnNext).setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }
    }

    private fun completeOnboarding() {
        prefs.setFirstLaunchComplete()
        findNavController().navigate(R.id.action_onboardingFragment_to_dashboardFragment)
    }
}

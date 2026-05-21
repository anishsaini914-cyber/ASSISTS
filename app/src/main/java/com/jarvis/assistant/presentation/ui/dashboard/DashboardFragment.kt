package com.jarvis.assistant.presentation.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    @Inject
    lateinit var viewModel: DashboardViewModel

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        observeViewModel()

        binding.cardWeather.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_chatFragment)
        }

        binding.cardVoice.setOnClickListener {
            findNavController().navigate(R.id.voiceAssistantFragment)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.weather.collectLatest { weather ->
                if (weather != null) {
                    binding.tvWeatherTemp.text = "${weather.temperature}°C"
                    binding.tvWeatherDesc.text = weather.description
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.activeModel.collectLatest { model ->
                binding.tvModelName.text = model?.fileName ?: "GPT-4o (Cloud)"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

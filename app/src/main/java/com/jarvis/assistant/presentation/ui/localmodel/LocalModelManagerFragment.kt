package com.jarvis.assistant.presentation.ui.localmodel

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jarvis.assistant.R
import com.jarvis.assistant.databinding.FragmentLocalModelManagerBinding
import com.jarvis.assistant.presentation.adapter.ModelCardAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocalModelManagerFragment : Fragment(R.layout.fragment_local_model_manager) {

    @Inject
    lateinit var viewModel: LocalModelViewModel

    private var _binding: FragmentLocalModelManagerBinding? = null
    private val binding get() = _binding!!

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            importModel(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLocalModelManagerBinding.bind(view)

        setupToolbar()
        setupButtons()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupButtons() {
        binding.btnImport.setOnClickListener {
            importLauncher.launch(arrayOf("*/*"))
        }

        binding.btnImportDownloads.setOnClickListener {
            // Scan downloads folder for models
        }
    }

    private fun setupRecyclerView() {
        val adapter = ModelCardAdapter(
            onSetActive = { model -> viewModel.setActiveModel(model) },
            onDelete = { model -> viewModel.deleteModel(model) }
        )
        binding.rvModels.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.models.collectLatest { models ->
                if (models.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvModels.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvModels.visibility = View.VISIBLE
                    (binding.rvModels.adapter as ModelCardAdapter).submitList(models)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.importProgress.collectLatest { progress ->
                if (progress.isComplete) {
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root, "Model imported successfully",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                } else if (progress.error != null) {
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root, "Import failed: ${progress.error}",
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun importModel(uri: Uri) {
        // Take persistable permission
        requireContext().contentResolver.takePersistableUriPermission(
            uri,
            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        viewModel.importModel(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

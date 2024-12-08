package com.fruitastic.ui.home

import FeedbackBottomSheet
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fruitastic.R
import com.fruitastic.data.ViewModelFactory
import com.fruitastic.data.local.entity.HistoryEntity
import com.fruitastic.databinding.FragmentHomeBinding
import com.fruitastic.getImageUri
import com.fruitastic.ui.history.HistoryViewModel
import com.yalantis.ucrop.UCrop
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Intent>
    private var cameraImageUri: Uri? = null

    private val viewModelHistory: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.currentImageUri == null) {
            binding.previewImageView.setImageResource(R.drawable.placeholder)
        } else {
            showImage()
        }

        cropActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val resultUri = UCrop.getOutput(data)
                    if (resultUri != null) {
                        viewModel.currentImageUri = resultUri
                        showImage()
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val data = result.data
                val cropError = UCrop.getError(data!!)
                cropError?.let { showToast(it.message ?: "Crop error") }
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.analyzeButton.setOnClickListener {
            viewModel.currentImageUri?.let {
                showResult(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
        binding.ivFeedback.setOnClickListener {
            val feedbackBottomSheet = FeedbackBottomSheet()
            feedbackBottomSheet.show(childFragmentManager, "FeedbackBottomSheet")
        }
    }

    //Start Gallery
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            showImage()
        } else {
            showToast(getString(R.string.no_photo_selected))
        }
    }

    // Start Camera
    private fun startCamera() {
        cameraImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(cameraImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.currentImageUri = cameraImageUri
            showImage()
        } else {
            showImage()
        }
    }

    //Show Image
    private fun showImage() {
        viewModel.currentImageUri?.let {
            Glide.with(binding.previewImageView.context)
                .load(Uri.parse(it.toString()))
                .transform(RoundedCorners(64)).into(binding.previewImageView)
            binding.cropButton.visibility = View.VISIBLE
            binding.cropButton.setOnClickListener {
                viewModel.currentImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
            binding.tvTitleResult.visibility = View.GONE
            binding.result.visibility = View.GONE
        } ?: run {
            binding.previewImageView.setImageResource(R.drawable.placeholder)
        }
    }

    // Start Crop
    private fun startCrop(uri: Uri) {
        val timestamp = System.currentTimeMillis()
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image$timestamp.jpg"))
        val uCropIntent = UCrop.of(uri, destinationUri)
            .withOptions(getCropOptions())
            .getIntent(requireContext())
        cropActivityResultLauncher.launch(uCropIntent)
    }


    private fun getCropOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setFreeStyleCropEnabled(true)
        options.setShowCropGrid(true)
        options.setShowCropFrame(true)
        options.setToolbarTitle("Crop Image")
        return options
    }

    // Show Result
    private fun showResult(uri: Uri) {
        val categories = arrayOf("Good", "Mild", "Rotten")
        val category = categories.random()
        val score = (50..100).random()
        binding.progressIndicator.visibility = View.VISIBLE

        val color = when (category) {
            "Good" -> ContextCompat.getColor(requireContext(), R.color.green)
            "Mild" -> ContextCompat.getColor(requireContext(), R.color.orange)
            "Rotten" -> ContextCompat.getColor(requireContext(), R.color.red)
            else -> ContextCompat.getColor(requireContext(), R.color.grey)
        }

        val drawable = binding.result.background as GradientDrawable
        drawable.setColor(color)

        val result = "$category $score%"
        binding.result.text = result
        binding.tvTitleResult.visibility = View.VISIBLE
        binding.result.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.GONE
        viewModel.getAutoSaveSetting().observe(viewLifecycleOwner) { isAutoSaveActive ->
            if (isAutoSaveActive) {
                saveToHistory(uri, category, score)
            } else {
                binding.saveButton.visibility = View.VISIBLE
                binding.saveButton.setOnClickListener {
                    saveToHistory(uri, category, score)
                    binding.saveButton.visibility = View.GONE
                }
            }
        }
    }

    // Save to History
    private fun saveToHistory(uri: Uri, result: String, score: Int) {
        val currentTime = System.currentTimeMillis()
        val historyEntity = HistoryEntity(
            image = uri.toString(),
            result = result,
            score = score,
            time = currentTime,
        )
        viewModelHistory.insertHistory(historyEntity)
        showToast("Success")
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
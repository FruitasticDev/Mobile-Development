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
import com.fruitastic.utils.getImageUri
import com.fruitastic.utils.reduceFileImage
import com.fruitastic.ui.history.HistoryViewModel
import com.fruitastic.utils.NetworkUtils
import com.fruitastic.utils.uriToFile
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Intent>
    private var cameraImageUri: Uri? = null
    private var isAutoSaveActive: Boolean = false
    private var isSaved = false

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
                cropError?.let { showToast(it.message ?: getString(R.string.error_crop)) }
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.analyzeButton.setOnClickListener {
            viewModel.currentImageUri?.let {
                if (NetworkUtils.isInternetAvailable(requireContext())) {
                    analyze(it)
                } else {
                    NetworkUtils.showInternetError(requireContext())
                }
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
        binding.ivFeedback.setOnClickListener {
            val feedbackBottomSheet = FeedbackBottomSheet()
            feedbackBottomSheet.show(childFragmentManager, "FeedbackBottomSheet")
        }

        viewModel.getAutoSaveSetting().observe(viewLifecycleOwner) { isActive ->
            isAutoSaveActive = isActive
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
            binding.saveButton.visibility = View.GONE
            isSaved = false
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
        options.setToolbarTitle(getString(R.string.title_crop_image))
        return options
    }

    // Analyze
    private fun analyze(uri: Uri) {
        isSaved = false
        val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)

        viewModel.clearResult()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.predict(multipartBody)

        viewModel.result.observe(viewLifecycleOwner) { result ->
            result?.let { (category, confidence) ->
                displayResult(uri, category, confidence)
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                showToast(it)
                viewModel.clearMessage()
            }
        }
    }

    private fun displayResult(uri: Uri, category: String, confidence: Float) {
        val confidencePercentage = (confidence * 100).toInt()
        val color = when {
            category.contains("Fresh") -> ContextCompat.getColor(requireContext(), R.color.green)
            category.contains("Mild") -> ContextCompat.getColor(requireContext(), R.color.orange)
            category.contains("Rotten") -> ContextCompat.getColor(requireContext(), R.color.red)
            else -> ContextCompat.getColor(requireContext(), R.color.grey)
        }

        binding.tvTitleResult.visibility = View.VISIBLE
        binding.result.visibility = View.VISIBLE
        binding.result.text = "$category: $confidencePercentage%"

        val drawable = binding.result.background as GradientDrawable
        drawable.setColor(color)

        isSaved = false

        if (isAutoSaveActive) {
            if (!isSaved) {
                saveToHistory(uri, category, confidencePercentage)
                isSaved = true
            }
        } else {
            binding.saveButton.visibility = View.VISIBLE
            binding.saveButton.setOnClickListener {
                // Only save if not already saved
                if (!isSaved) {
                    saveToHistory(uri, category, confidencePercentage)
                    binding.saveButton.visibility = View.GONE
                    isSaved = true
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
        isSaved = true
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
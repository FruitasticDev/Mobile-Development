package com.fruitastic.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fruitastic.data.Repository
import com.fruitastic.data.remote.request.FeedbackRequest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


class HomeViewModel(private val repository: Repository) :ViewModel() {
    var currentImageUri: Uri? = null

    private val _result = MutableLiveData<Pair<String, Float>>()
    val result: LiveData<Pair<String, Float>> get() = _result

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _messageFeedback = MutableLiveData<String?>()
    val messageFeedback: LiveData<String?> get() = _messageFeedback

    fun predict(image: MultipartBody.Part) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.predict(image)
                val confidence = response.confidence ?: 0.0f
                val category = response.jsonMemberClass?.cleanCategory()?: "Unknown"
                _result.postValue(category to confidence)
                _message.postValue("Prediction Successfully")
            } catch (e: Exception) {
                _message.postValue("Error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAutoSaveSetting(): LiveData<Boolean> {
        return repository.getAutoSaveSetting().asLiveData()
    }

    fun feedback(request: FeedbackRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.feedback(request)
                if (response.isSuccessful && response.body() != null) {
                    _messageFeedback.postValue(response.body()?.message ?: "Feedback sent successfully")
                } else {
                    _messageFeedback.postValue(response.body()?.message ?: "Feedback submission failed")
                }
            } catch (e: Exception) {
                _messageFeedback.postValue("Network error, please try again")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun String.cleanCategory(): String {
        val parts = this.replace("Q", "").replace("DB", "").split("_")
        val category = parts.lastOrNull() ?: ""
        val name = parts.dropLast(1).joinToString(" ") { it -> it.replaceFirstChar { it.uppercaseChar() } }
        return "$name $category".trim()
    }

    fun clearMessage() {
        _message.value = null
    }
}
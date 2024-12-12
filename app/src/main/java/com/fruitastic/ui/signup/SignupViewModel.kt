package com.fruitastic.ui.signup

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitastic.R
import com.fruitastic.data.Repository
import com.fruitastic.data.remote.request.RegisterRequest
import com.fruitastic.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: Repository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> get() = _registerResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    fun register(request: RegisterRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(request)
                if (response.isSuccessful) {
                    _registerResult.postValue(response.body())
                } else {
                    _errorMessage.postValue(response.errorBody()?.string() ?: context.getString(R.string.unknown_error))
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message ?: R.string.network_error}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
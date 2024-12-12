package com.fruitastic.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitastic.data.Repository
import com.fruitastic.data.pref.UserModel
import com.fruitastic.data.remote.request.LoginRequest
import com.fruitastic.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> get() = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(request: LoginRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(request)
                if (response.isSuccessful) {
                    _loginResult.postValue(response.body())
                } else {
                    _errorMessage.postValue(response.errorBody()?.string() ?: "Unknown Error")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message ?: "Network Error"}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
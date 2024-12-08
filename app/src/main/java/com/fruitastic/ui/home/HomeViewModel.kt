package com.fruitastic.ui.home


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fruitastic.data.Repository

class HomeViewModel(private val repository: Repository) :ViewModel() {
    var currentImageUri: Uri? = null

    fun getAutoSaveSetting(): LiveData<Boolean> {
        return repository.getAutoSaveSetting().asLiveData()
    }
}
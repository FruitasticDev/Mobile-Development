package com.fruitastic.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fruitastic.data.Repository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: Repository) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> {
        return repository.getThemeSettings().asLiveData().also {
            Log.d("SettingViewModel", "Current Theme Setting: ${it.value}")
        }
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            repository.saveThemeSettings(isDarkModeActive)
            Log.d("SettingViewModel", "Theme saved: $isDarkModeActive")
        }
    }

    fun getAutoSaveSetting(): LiveData<Boolean> {
        return repository.getAutoSaveSetting().asLiveData()
    }

    fun saveAutoSaveSetting(isAutoSaveActive: Boolean) {
        viewModelScope.launch {
            repository.saveAutoSaveSetting(isAutoSaveActive)
        }
    }
}
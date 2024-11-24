package com.fruitastic.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData().also {
            Log.d("SettingViewModel", "Current Theme Setting: ${it.value}")
        }
    }


    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
            Log.d("SettingViewModel", "Theme saved: $isDarkModeActive")
        }
    }

    fun getAutoSaveSetting(): LiveData<Boolean> {
        return pref.getAutoSaveSetting().asLiveData()
    }

    fun saveAutoSaveSetting(isAutoSaveActive: Boolean) {
        viewModelScope.launch {
            pref.saveAutoSaveSetting(isAutoSaveActive)
        }
    }
}
package com.fruitastic.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fruitastic.ui.history.HistoryViewModel
import com.fruitastic.ui.home.HomeViewModel
import com.fruitastic.ui.login.LoginViewModel
import com.fruitastic.ui.main.MainViewModel
import com.fruitastic.ui.setting.SettingViewModel
import com.fruitastic.ui.signup.SignupViewModel

class ViewModelFactory private constructor(private val repository: Repository):
    ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom((HistoryViewModel::class.java))) {
            return HistoryViewModel(repository) as T
        } else if (modelClass.isAssignableFrom((MainViewModel::class.java))) {
            return MainViewModel(repository) as T
        } else if (modelClass.isAssignableFrom((SettingViewModel::class.java))) {
            return SettingViewModel(repository) as T
        } else if (modelClass.isAssignableFrom((HomeViewModel::class.java))) {
            return HomeViewModel(repository) as T
        } else if (modelClass.isAssignableFrom((LoginViewModel::class.java))) {
            return LoginViewModel(repository) as T
        } else if (modelClass.isAssignableFrom((SignupViewModel::class.java))) {
            return SignupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}


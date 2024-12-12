package com.fruitastic.data

import androidx.lifecycle.LiveData
import com.fruitastic.data.local.entity.HistoryEntity
import com.fruitastic.data.local.room.HistoryDao
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.UserModel
import com.fruitastic.data.remote.request.LoginRequest
import com.fruitastic.data.remote.request.RegisterRequest
import com.fruitastic.data.remote.response.LoginResponse
import com.fruitastic.data.remote.response.RegisterResponse
import com.fruitastic.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repository private constructor(
    private val apiService: ApiService,
    private val historyDao: HistoryDao,
    private val appPreferences: AppPreferences

) {

    fun getThemeSettings():Flow<Boolean> {
        return appPreferences.getThemeSetting()
    }

    suspend fun saveThemeSettings(isDarkModeActive: Boolean) {
        return appPreferences.saveThemeSetting(isDarkModeActive)
    }

    fun getAutoSaveSetting():Flow<Boolean> {
        return appPreferences.getAutoSaveSetting()
    }

    suspend fun saveAutoSaveSetting(isDarkModeActive: Boolean) {
        return appPreferences.saveAutoSaveSetting(isDarkModeActive)
    }

    fun getLanguageSetting(): Flow<String> {
        return appPreferences.getLanguageSetting()
    }

    suspend fun saveLanguageSetting(language: String) {
        appPreferences.saveLanguageSetting(language)
    }

    fun getSession(): Flow<UserModel> {
        return appPreferences.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        appPreferences.saveSession(user)
    }

    suspend fun logout() {
        appPreferences.logout()
    }

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    fun getHistory(): LiveData<List<HistoryEntity>> {
        return historyDao.getHistory()
    }

    suspend fun insertHistory(historyEntity: HistoryEntity) {
        historyDao.insertHistory(listOf(historyEntity))
    }


    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            historyDao: HistoryDao,
            appPreferences: AppPreferences
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, historyDao, appPreferences)
            }.also { instance = it }
    }
}
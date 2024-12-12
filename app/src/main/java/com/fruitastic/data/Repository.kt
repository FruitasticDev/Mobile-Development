package com.fruitastic.data

import androidx.lifecycle.LiveData
import com.fruitastic.data.local.entity.HistoryEntity
import com.fruitastic.data.local.room.HistoryDao
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.UserModel
import com.fruitastic.data.remote.request.FeedbackRequest
import com.fruitastic.data.remote.request.LoginRequest
import com.fruitastic.data.remote.request.RegisterRequest
import com.fruitastic.data.remote.response.FeedbackResponse
import com.fruitastic.data.remote.response.LoginResponse
import com.fruitastic.data.remote.response.PredictResponse
import com.fruitastic.data.remote.response.RegisterResponse
import com.fruitastic.data.remote.retrofit.ApiServiceAuth
import com.fruitastic.data.remote.retrofit.ApiServiceModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response

class Repository private constructor(
    private val apiServiceAuth: ApiServiceAuth,
    private val apiServiceModel: ApiServiceModel,
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
        return apiServiceAuth.register(request)
    }

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiServiceAuth.login(request)
    }

    suspend fun feedback(request: FeedbackRequest): Response<FeedbackResponse> {
        return apiServiceAuth.feedback(request)
    }

    fun getHistory(): LiveData<List<HistoryEntity>> {
        return historyDao.getHistory()
    }

    suspend fun insertHistory(historyEntity: HistoryEntity) {
        historyDao.insertHistory(listOf(historyEntity))
    }

    suspend fun predict(image: MultipartBody.Part): PredictResponse {
        return apiServiceModel.predict(image)
    }


    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiServiceAuth: ApiServiceAuth,
            apiServiceModel: ApiServiceModel,
            historyDao: HistoryDao,
            appPreferences: AppPreferences
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiServiceAuth, apiServiceModel, historyDao, appPreferences)
            }.also { instance = it }
    }
}
package com.fruitastic.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.fruitastic.data.AppExecutors
import com.fruitastic.data.local.entity.HistoryEntity
import com.fruitastic.data.local.room.HistoryDao
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.UserModel
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
//    private val apiService: ApiService,
    private val historyDao: HistoryDao,
    private val appExecutors: AppExecutors,
    private val appPreferences: AppPreferences

) {

    private val result = MediatorLiveData<Result<List<HistoryEntity>>>()

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

    fun getSession(): Flow<UserModel> {
        return appPreferences.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        appPreferences.saveSession(user)
    }

    suspend fun logout() {
        appPreferences.logout()
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
//            apiService: ApiService,
            historyDao: HistoryDao,
            appExecutors: AppExecutors,
            appPreferences: AppPreferences
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(historyDao,appExecutors, appPreferences)
            }.also { instance = it }
    }
}
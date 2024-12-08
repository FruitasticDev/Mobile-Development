package com.fruitastic.data

import android.content.Context
import androidx.datastore.dataStore
import com.fruitastic.data.local.room.HistoryDatabase
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): Repository {
//        val apiService = ApiConfig.getApiService()
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        val appPreferences = AppPreferences.getInstance(context.dataStore)
        return Repository.getInstance(dao, appPreferences)
    }
}
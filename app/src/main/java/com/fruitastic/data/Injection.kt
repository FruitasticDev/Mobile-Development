package com.fruitastic.data

import android.content.Context
import com.fruitastic.data.local.room.HistoryDatabase
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.dataStore
import com.fruitastic.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        val pref = AppPreferences.getInstance(context.dataStore)
        return Repository.getInstance(apiService, dao, pref)
    }
}
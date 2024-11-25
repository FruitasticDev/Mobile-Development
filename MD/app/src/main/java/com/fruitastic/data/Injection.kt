package com.fruitastic.data

import android.content.Context
import com.fruitastic.data.local.room.HistoryDatabase

object Injection {
    fun provideRepository(context: Context): Repository {
//        val apiService = ApiConfig.getApiService()
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        val appExecutors = AppExecutors()
        return Repository.getInstance(dao, appExecutors)
    }
}
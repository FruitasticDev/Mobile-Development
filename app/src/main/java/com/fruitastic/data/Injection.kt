package com.fruitastic.data

import android.content.Context
import com.fruitastic.data.local.room.HistoryDatabase
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.dataStore
import com.fruitastic.data.remote.retrofit.ApiConfigAuth
import com.fruitastic.data.remote.retrofit.ApiConfigModel

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiServiceAuth = ApiConfigAuth.getApiService()
        val apiServiceModel = ApiConfigModel.getApiService()
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        val pref = AppPreferences.getInstance(context.dataStore)
        return Repository.getInstance(apiServiceAuth, apiServiceModel,dao, pref)
    }
}
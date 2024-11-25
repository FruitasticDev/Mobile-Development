package com.fruitastic.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.fruitastic.data.AppExecutors
import com.fruitastic.data.local.entity.HistoryEntity
import com.fruitastic.data.local.room.HistoryDao

class Repository private constructor(
//    private val apiService: ApiService,
    private val historyDao: HistoryDao,
    private val appExecutors: AppExecutors

) {

    private val result = MediatorLiveData<Result<List<HistoryEntity>>>()

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
            appExecutors: AppExecutors
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(historyDao,appExecutors)
            }.also { instance = it }
    }
}
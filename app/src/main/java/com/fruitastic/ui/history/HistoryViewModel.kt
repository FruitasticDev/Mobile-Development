package com.fruitastic.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitastic.data.Repository
import com.fruitastic.data.local.entity.HistoryEntity
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: Repository) : ViewModel() {

    fun getHistory() = repository.getHistory()

    fun insertHistory(historyEntity: HistoryEntity) {
        viewModelScope.launch {
            repository.insertHistory(historyEntity)
        }
    }
}
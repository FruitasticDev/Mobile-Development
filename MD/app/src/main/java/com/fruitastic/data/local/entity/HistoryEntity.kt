package com.fruitastic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity(
    @field:ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @field:ColumnInfo(name = "image")
    val image: String,

    @field:ColumnInfo(name = "result")
    val result: String,

    @field:ColumnInfo(name = "score")
    val score: Int,

    @field:ColumnInfo(name = "time")
    val time: Long
)
package com.fruitastic.data.remote.request

import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
    @SerializedName("feedback")
    val feedback: String,
)

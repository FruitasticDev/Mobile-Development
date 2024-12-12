package com.fruitastic.data.remote.response

import com.google.gson.annotations.SerializedName

data class FeedbackResponse(

	@field:SerializedName("feedback")
	val feedback: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

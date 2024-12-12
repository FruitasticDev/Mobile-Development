package com.fruitastic.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("confidence")
	val confidence: Float? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null
)

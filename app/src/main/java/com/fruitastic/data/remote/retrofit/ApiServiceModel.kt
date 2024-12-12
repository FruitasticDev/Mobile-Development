package com.fruitastic.data.remote.retrofit

import com.fruitastic.data.remote.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiServiceModel {

    @Multipart
    @POST("predict")
    suspend fun predict(
        @Part file: MultipartBody.Part
    ): PredictResponse
}
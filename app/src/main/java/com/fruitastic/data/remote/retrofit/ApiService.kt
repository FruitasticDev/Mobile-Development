package com.fruitastic.data.remote.retrofit

import com.fruitastic.data.remote.request.LoginRequest
import com.fruitastic.data.remote.request.RegisterRequest
import com.fruitastic.data.remote.response.LoginResponse
import com.fruitastic.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/register")
    suspend fun register
        (@Body request: RegisterRequest)
    : Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login
        (@Body request: LoginRequest)
    : Response<LoginResponse>
}
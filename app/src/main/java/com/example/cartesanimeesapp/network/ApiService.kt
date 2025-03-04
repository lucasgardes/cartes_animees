package com.example.cartesanimeesapp.network

import com.example.cartesanimeesapp.models.LoginRequest
import com.example.cartesanimeesapp.models.LoginResponse
import com.example.cartesanimeesapp.models.Series
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api.php?action=login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("api.php?action=series")
    fun getSeries(): Call<List<Series>>
}

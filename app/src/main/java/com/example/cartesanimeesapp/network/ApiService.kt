package com.example.cartesanimeesapp.network

import com.example.cartesanimeesapp.models.LoginRequest
import com.example.cartesanimeesapp.models.LoginResponse
import com.example.cartesanimeesapp.models.Patient
import com.example.cartesanimeesapp.models.Serie
import com.example.cartesanimeesapp.models.Subscription
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api_public.php")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("action") action: String = "login",
        @Query("token") token: String
    ): Call<LoginResponse>

    @GET("api_public.php")
    fun getSeries(
        @Query("action") action: String = "series",
        @Query("patient_id") patientId: Int,
        @Query("token") token: String
    ): Call<List<Serie>>

    @GET("api_public.php")
    fun getSerieById(
        @Query("action") action: String = "serie",
        @Query("id") id: Int,
        @Query("token") token: String
    ): Call<Serie>

    @POST("api_public.php")
    @FormUrlEncoded
    fun startSerieSession(
        @Field("action") action: String = "start_session",
        @Field("patient_id") patientId: Int,
        @Field("serie_id") serieId: Int,
        @Query("token") token: String
    ): Call<Int>

    @POST("api_public.php")
    @FormUrlEncoded
    fun endSerieSession(
        @Field("action") action: String = "end_session",
        @Field("session_id") sessionId: Int,
        @Query("token") token: String
    ): Call<Void>

    @POST("api_public.php")
    @FormUrlEncoded
    fun replaySound(
        @Field("action") action: String = "replay_sound",
        @Field("patient_id") patientId: Int,
        @Field("animation_id") animationId: Int,
        @Query("token") token: String
    ): Call<Void>

    @POST("api_public.php")
    @FormUrlEncoded
    fun loginPatient(
        @Field("action") action: String = "login_patient",
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("token") token: String
    ): Call<LoginResponse>

    @POST("api_public.php")
    @FormUrlEncoded
    fun getPatientInfo(
        @Field("action") action: String = "get_patient_info",
        @Field("patient_id") patientId: Int,
        @Query("token") token: String
    ): Call<Patient>

    @POST("api_public.php")
    @FormUrlEncoded
    fun getSubscription(
        @Field("action") action: String = "get_subscription",
        @Field("patient_id") patientId: Int,
        @Query("token") token: String
    ): Call<Subscription?>

    @POST("api_public.php")
    @FormUrlEncoded
    fun requestResiliation(
        @Field("action") action: String = "resiliation_abonnement",
        @Field("patient_id") patientId: Int,
        @Query("token") token: String
    ): Call<Void>

    @POST("api_public.php")
    @FormUrlEncoded
    fun setPatientPassword(
        @Field("action") action: String = "set_patient_password",
        @Field("patient_id") patientId: Int,
        @Field("password") password: String,
        @Query("token") token: String
    ): Call<Void>
}

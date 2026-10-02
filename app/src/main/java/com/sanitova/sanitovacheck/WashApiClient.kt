package com.sanitova.sanitovacheck

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface WashApiService {

    @POST("assess")
    suspend fun assess(@Body request: AssessRequest): Response<AssessResponse>

    @POST("chat")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>

    @GET("health")
    suspend fun health(): Response<Map<String, String>>
}

object WashApiClient {

    private const val BASE_URL = "https://wash-risk-agent-hwfcbnykfb.ap-southeast-1.fcapp.run/"

    // The AI endpoints (assess, chat) call a large model and can take well over
    // 20s to produce a full answer. The original 20s read timeout cut those
    // responses off mid-generation and surfaced a misleading "request timed
    // out" to the user. Short prompts still return in a few seconds, so a
    // longer timeout costs nothing on the fast path.
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    val service: WashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WashApiService::class.java)
    }
}

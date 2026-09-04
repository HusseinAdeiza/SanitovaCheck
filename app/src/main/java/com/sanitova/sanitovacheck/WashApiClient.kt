package com.sanitova.sanitovacheck

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    val service: WashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WashApiService::class.java)
    }
}

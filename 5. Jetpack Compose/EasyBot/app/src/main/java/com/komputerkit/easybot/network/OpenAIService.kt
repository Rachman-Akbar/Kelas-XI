package com.komputerkit.easybot.network

import com.komputerkit.easybot.data.model.ChatRequest
import com.komputerkit.easybot.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
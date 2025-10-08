package com.komputerkit.easybot.data.openai

import com.google.gson.annotations.SerializedName

data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 150,
    val temperature: Double = 0.7
)

data class ChatMessage(
    val role: String, // "user", "assistant", "system"
    val content: String
)

data class OpenAIResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: ChatMessage,
    @SerializedName("finish_reason")
    val finishReason: String
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

data class OpenAIError(
    val error: ErrorDetail
)

data class ErrorDetail(
    val message: String,
    val type: String,
    val code: String? = null
)
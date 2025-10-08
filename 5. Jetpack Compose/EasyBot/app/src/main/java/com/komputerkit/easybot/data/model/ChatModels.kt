package com.komputerkit.easybot.data.model

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7
)

data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
package com.komputerkit.newsnow.data.repository

import com.komputerkit.newsnow.data.api.NewsApiService
import com.komputerkit.newsnow.data.model.NewsResponse
import com.komputerkit.newsnow.util.Constants
import retrofit2.Response

class NewsRepository(private val apiService: NewsApiService) {
    
    suspend fun getTopHeadlines(
        country: String = Constants.DEFAULT_COUNTRY,
        category: String? = null
    ): Response<NewsResponse> {
        return apiService.getTopHeadlines(
            apiKey = Constants.NEWS_API_KEY,
            country = country,
            category = category
        )
    }
    
    suspend fun searchNews(query: String): Response<NewsResponse> {
        return apiService.getEverything(
            apiKey = Constants.NEWS_API_KEY,
            query = query
        )
    }
}
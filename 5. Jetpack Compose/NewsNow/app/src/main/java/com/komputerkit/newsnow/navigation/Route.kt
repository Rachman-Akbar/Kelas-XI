package com.komputerkit.newsnow.navigation

object Routes {
    const val HOME_PAGE = "home_page"
    const val NEWS_ARTICLE_PAGE = "news_article_page"
    
    fun newsArticlePageWithUrl(url: String): String {
        return "$NEWS_ARTICLE_PAGE/{url}"
    }
    
    fun createNewsArticleRoute(url: String): String {
        return "$NEWS_ARTICLE_PAGE/$url"
    }
}
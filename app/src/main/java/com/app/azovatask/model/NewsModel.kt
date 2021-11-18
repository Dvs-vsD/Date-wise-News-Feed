package com.app.azovatask.model

data class NewsModel(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsDataModel>,
)
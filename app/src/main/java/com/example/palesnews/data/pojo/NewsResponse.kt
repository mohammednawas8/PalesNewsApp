package com.example.palesnews.data.pojo

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)
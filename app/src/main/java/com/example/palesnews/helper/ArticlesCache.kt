package com.example.palesnews.helper

import com.example.palesnews.data.pojo.Article
import com.example.palesnews.repositories.ArticlesRepository

class ArticlesCache(
    private val repository: ArticlesRepository
) {
    suspend fun updateArticlesCacheStatues(articles: List<Article>) {
        repository.deleteAllArticles()
        repository.insertListOfArticles(articles)
    }
}
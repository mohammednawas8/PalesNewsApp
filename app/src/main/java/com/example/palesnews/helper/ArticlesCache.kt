package com.example.palesnews.helper

import android.util.Log
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.repositories.ArticlesRepository

class ArticlesCache(
    private val repository: ArticlesRepository
) {
    suspend fun updateTopHeadlineArticlesCache(articles: List<Article>) {
        repository.deleteAllArticles()
        repository.insertListOfArticles(articles)
    }

    suspend fun updateCategoryArticlesCache(category: String, articles: List<Article>) {
        val articlesListAfterCategory = articles.map { article ->
            Article(
                article.author,
                article.content,
                article.publishedAt,
                article.publishedAt,
                article.source,
                article.title,
                article.url,
                article.urlToImage,
                category
            )
        }
        repository.deleteCategoryArticles(category)
        repository.insertListOfArticles(articlesListAfterCategory)
        Log.d("ArticlesCache","$category Cache is updated")
    }
}
package com.example.palesnews.repositories

import com.example.palesnews.data.database.ArticlesDatabase
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.data.remote.NewsApi
import javax.inject.Inject

class ArticlesRepository (
    private val newsApi: NewsApi,
    private val articlesDatabase: ArticlesDatabase
) {
    private val articlesDao = articlesDatabase.articlesDao

    suspend fun fetchTopHeadlineNews(
        country: String,
        page:Int
        ) = newsApi.fetchTopHeadlinesNews(country,page)

    suspend fun fetchNewsByCategory(
        country:String,
        page:Int,
        category:String
    ) = newsApi.fetchNewsByCategory(country, page, category)

    suspend fun searchNews(
        searchQuery:String
    ) = newsApi.searchNews(searchQuery)

    suspend fun insertArticle(
        article:Article
    ) = articlesDao.upsertArticle(article)

    suspend fun insertListOfArticles(
        articles:List<Article>
    ) = articles.forEach { articlesDao.upsertArticle(it) }

    suspend fun deleteArticle(
        article: Article
    ) = articlesDao.deleteArticle(article)

    suspend fun deleteAllArticles() = articlesDao.deleteAllArticles()

    fun getAllArticles() = articlesDao.getAllArticles()

    fun getArticlesByCategory (
        category:String
    ) = articlesDao.getArticlesByCategory(category)

}
package com.example.palesnews.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.palesnews.data.pojo.Article

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticle(article: Article)

    @Delete
    suspend fun deleteArticle(article: Article)

    @Transaction
    @Query("SELECT * FROM Article")
    fun getAllArticles(): LiveData<List<Article>>

    @Transaction
    @Query("SELECT * FROM Article" + " WHERE category = :category")
    fun getArticlesByCategory(category: String): LiveData<List<Article>>

    @Transaction
    @Query("DELETE FROM Article")
    suspend fun deleteAllArticles()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticles(articles:List<Article>)

    @Query("SELECT * FROM Article ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomArticle():Article
}
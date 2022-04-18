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
    @Query("SELECT * FROM Article" + " order by publishedAt")
    fun getAllArticles(): LiveData<List<Article>>

    @Transaction
    @Query("SELECT * FROM Article" + " WHERE category = :category order by publishedAt")
    fun getArticlesByCategory(category: String): LiveData<List<Article>>

    @Transaction
    @Query("DELETE FROM Article")
    suspend fun deleteAllArticles()
}
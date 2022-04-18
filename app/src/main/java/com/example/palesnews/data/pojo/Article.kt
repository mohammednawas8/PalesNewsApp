package com.example.palesnews.data.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val urlToImage: String?,
    val category: String?
)
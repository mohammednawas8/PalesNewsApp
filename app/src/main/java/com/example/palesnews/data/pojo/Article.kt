package com.example.palesnews.data.pojo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Entity
@Parcelize
data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: @RawValue Source? = Source("",""),
    val title: String?,
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val urlToImage: String?,
    var category: String?
) : Parcelable
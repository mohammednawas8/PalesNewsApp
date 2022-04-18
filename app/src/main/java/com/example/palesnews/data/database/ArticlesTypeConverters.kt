package com.example.palesnews.data.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.palesnews.data.pojo.Source

@TypeConverters
class ArticlesTypeConverters {

    @TypeConverter
    fun fromSourceToString(source: Source?): String {
        return source?.name ?: ""
    }

    @TypeConverter
    fun fromStringToSource(source: String?): Source {
        return source?.let { Source("", it) } ?: Source("", "")
    }
}
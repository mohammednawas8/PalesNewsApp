package com.example.palesnews.di.modules

import android.content.Context
import com.example.palesnews.data.database.ArticlesDatabase
import com.example.palesnews.data.remote.NewsApi
import com.example.palesnews.util.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi() : NewsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideArticlesDatabase(
        @ApplicationContext context:Context
    ) = ArticlesDatabase.getArticlesDatabaseInstance(context)


}
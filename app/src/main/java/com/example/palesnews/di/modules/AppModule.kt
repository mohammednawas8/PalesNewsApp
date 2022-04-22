package com.example.palesnews.di.modules

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.palesnews.R
import com.example.palesnews.data.database.ArticlesDatabase
import com.example.palesnews.data.remote.NewsApi
import com.example.palesnews.helper.ArticlesCache
import com.example.palesnews.helper.Navigation
import com.example.palesnews.repositories.ArticlesRepository
import com.example.palesnews.util.Constants.Companion.BASE_URL
import com.example.palesnews.util.Constants.Companion.COUNTRY_CODE
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
    fun provideNewsApi(): NewsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideArticlesDatabase(
        @ApplicationContext context: Context
    ): ArticlesDatabase = ArticlesDatabase.getArticlesDatabaseInstance(context)


    @Provides
    @Singleton
    fun provideArticlesRepository(
        newsApi: NewsApi,
        articlesDatabase: ArticlesDatabase
    ) = ArticlesRepository(newsApi, articlesDatabase)

    @Provides
    @Singleton
    fun provideArticlesCacheObject(
        repository: ArticlesRepository
    ) = ArticlesCache(repository)

    @Provides
    @Singleton
    fun provideGlide(
        @ApplicationContext context: Context
    ) = Glide.with(context).applyDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.loading)
            .error(R.drawable.error_loading)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Provides
    @Singleton
    fun provideNavigation() = Navigation()

    @Provides
    @Singleton
    fun provideCountryCode(
        @ApplicationContext context: Context
    ) = context.getSharedPreferences(COUNTRY_CODE, MODE_PRIVATE).getString(COUNTRY_CODE, "us")
        ?: "us"

}
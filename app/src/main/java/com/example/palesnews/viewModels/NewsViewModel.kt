package com.example.palesnews.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.data.pojo.NewsResponse
import com.example.palesnews.helper.ArticlesCache
import com.example.palesnews.repositories.ArticlesRepository
import com.example.palesnews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val articlesCache: ArticlesCache
) : ViewModel() {

    init {
        fetchTopHeadlineNews("us", 1)
    }

    val topHeadlineNews = articlesRepository.getAllArticles()

    private val _topHeadlineNewsProgress = MutableLiveData<Resource<List<Article>>>()
    val topHeadlineNewsProgress: LiveData<Resource<List<Article>>> = _topHeadlineNewsProgress

    private var topHeadlineNewsPage = 1

    fun fetchTopHeadlineNews(country: String, page: Int = topHeadlineNewsPage) =
        viewModelScope.launch {
            // In the Loading case - while fetching the news from the Server we will send the cached articles to the Ui
            _topHeadlineNewsProgress?.postValue(Resource.Loading())
            try {
                val response = articlesRepository.fetchTopHeadlineNews(country, page)
                handleNewsResponse(response)
            } catch (e: Exception) {
                _topHeadlineNewsProgress.postValue(Resource.Error(e.message.toString()))
            }
        }

    private fun handleNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful)
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                _topHeadlineNewsProgress.postValue(Resource.Success(articles))
                topHeadlineNewsPage++
                updateCacheStatues(articles)
            }
        else
            _topHeadlineNewsProgress.postValue(Resource.Error(response.message()))
    }

    private fun updateCacheStatues(articles: List<Article>) = viewModelScope.launch {
        articlesCache.updateArticlesCacheStatues(articles)
    }


}
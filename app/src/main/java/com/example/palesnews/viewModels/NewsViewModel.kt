package com.example.palesnews.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.data.pojo.NewsResponse
import com.example.palesnews.helper.ArticlesCache
import com.example.palesnews.repositories.ArticlesRepository
import com.example.palesnews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val articlesCache: ArticlesCache
) : ViewModel() {

    private val _topHeadlineNews = MutableLiveData<Resource<List<Article>>>()
    val topHeadLineNews: LiveData<Resource<List<Article>>> = _topHeadlineNews

    fun fetchTopHeadlineNews(country: String, page: Int) = viewModelScope.launch {
        // In the Loading case - while fetching the news from the Server we will send the cached articles to the Ui
        _topHeadlineNews.postValue(Resource.Loading(articlesRepository.getAllArticles().value))
        try {
            val response = articlesRepository.fetchTopHeadlineNews(country, page)
            handleNewsResponse(response)
        } catch (e: Exception) {
            _topHeadlineNews.postValue(Resource.Error(e.message.toString()))
        }
    }

    private fun handleNewsResponse(response: Response<NewsResponse>){
        if (response.isSuccessful)
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                _topHeadlineNews.postValue(Resource.Success(articles))
                updateCacheStatues(articles)
            }
        else
            _topHeadlineNews.postValue(Resource.Error(response.message()))
    }

    private fun updateCacheStatues(articles:List<Article>) = viewModelScope.launch {
        articlesCache.updateArticlesCacheStatues(articles)
    }


}
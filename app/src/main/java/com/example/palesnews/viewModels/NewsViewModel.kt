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

    val topHeadlineNews = articlesRepository.getAllArticles()

    private val _topHeadlineNewsProgress = MutableLiveData<Resource<List<Article>>>()
    val topHeadlineNewsProgress: LiveData<Resource<List<Article>>> = _topHeadlineNewsProgress

    private var _featuredArticle = MutableLiveData<Article>()
    val featuredArticle: LiveData<Article> = _featuredArticle

    private var topHeadlineNewsPage = 1
    private var shouldPaging = true

    init {
        fetchTopHeadlineNews("us", topHeadlineNewsPage)
        fetchFeaturedArticle()
    }

    //This function will pick a random meal from the articles saved into the local database and consider it as a Featured Article
    //That's not how you should do it if you are working with a real app. but since the REST API doesn't provide us a Featured Article
    //i will use this function.
    private fun fetchFeaturedArticle() = viewModelScope.launch {
        val featuredArticle = articlesRepository.getRandomArticle()
        _featuredArticle.postValue(featuredArticle)
    }


    fun fetchTopHeadlineNews(country: String, page: Int = topHeadlineNewsPage) =
        viewModelScope.launch {
            try {
                if (shouldPaging) {
                    _topHeadlineNewsProgress.postValue(Resource.Loading())
                    val response = articlesRepository.fetchTopHeadlineNews(country, page)
                    handleNewsResponse(response)
                } else
                    _topHeadlineNewsProgress.postValue(Resource.Error("No Paging"))
            } catch (e: Exception) {
                _topHeadlineNewsProgress.postValue(Resource.Error(e.message.toString()))
            }
        }

    //In this function we will handle the response and get the articles if the response is isSuccessful
    //Then we will add the new articles to oldArticles list which will keep the previous loaded articles and add the new articles to them
    //This will make sure that when we want to get articles in page 2 the old articles were in page 1 won't get lost. and same for page 3,4,...
    private var oldArticles = ArrayList<Article>()
    private fun handleNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful)
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles

                //Updating Paging statues
                if (newsResponse.status != "ok" || newsResponse.articles.isEmpty()) {
                    shouldPaging = false
                    _topHeadlineNewsProgress.postValue(Resource.Error("No Paging"))
                    return
                }
                oldArticles = oldArticles.plus(articles) as ArrayList<Article>
                _topHeadlineNewsProgress.postValue(Resource.Success(oldArticles))
                topHeadlineNewsPage++
                updateCacheStatues(oldArticles)
            }
        else
            _topHeadlineNewsProgress.postValue(Resource.Error(response.message()))
    }


    private fun updateCacheStatues(articles: List<Article>) = viewModelScope.launch {
        articlesCache.updateArticlesCacheStatues(articles)
    }


}
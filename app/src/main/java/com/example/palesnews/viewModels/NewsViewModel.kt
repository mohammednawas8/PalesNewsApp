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
import com.example.palesnews.util.Constants.Companion.CATEGORY_ARTICLES
import com.example.palesnews.util.Constants.Companion.TOP_HEADLINE_ARTICLES
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

    private val _featuredArticle = MutableLiveData<Article>()
    val featuredArticle: LiveData<Article> = _featuredArticle

    private val _categoryNewsProgress = MutableLiveData<Resource<List<Article>>>()
    val categoryNewsProgress: LiveData<Resource<List<Article>>> = _categoryNewsProgress

    private val _categoryNews = MutableLiveData<List<Article>>()
    val categoryNews: LiveData<List<Article>> = _categoryNews

    private var topHeadlineNewsPage = 1
    private var shouldPagingHeadlineNews = true

    var categoryNewsPage = 1
    private var shouldPagingCategoryNews = true

    init {
        fetchTopHeadlineNews("us", topHeadlineNewsPage)
        fetchFeaturedArticle()
    }

    //This function will pick a random meal from the articles saved into the local database and consider it as a Featured Article
    //That's not how you should do it in a real app. but since the REST API doesn't provide us a Featured Article
    //i will use this function. but featuredArticle will be null when we run the app for the fist time
    //because 0 articles are stored in the database.
    private fun fetchFeaturedArticle() = viewModelScope.launch {
        val featuredArticle = articlesRepository.getRandomArticle()
        featuredArticle?.let { _featuredArticle.postValue(it) }
    }


    fun fetchTopHeadlineNews(country: String, page: Int = topHeadlineNewsPage) =
        viewModelScope.launch {
            try {
                if (shouldPagingHeadlineNews) {
                    _topHeadlineNewsProgress.postValue(Resource.Loading())
                    val response = articlesRepository.fetchTopHeadlineNews(country, page)
                    handleTopHeadlineNewsResponse(response)
                } else
                    _topHeadlineNewsProgress.postValue(Resource.Error("No Paging"))
            } catch (e: Exception) {
                _topHeadlineNewsProgress.postValue(Resource.Error(e.message.toString()))
            }
        }

    //In this function we will handle the response and get the articles if the response is isSuccessful
    //Then we will add the new articles to oldArticles list which will keep the previous loaded articles and add the new articles to them
    //This will make sure that when we want to get articles in page 2 the old articles were in page 1 won't get lost. and same for page 3,4,...
    private var oldTopHeadlineArticles = ArrayList<Article>()
    private fun handleTopHeadlineNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful)
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles

                //Updating Paging statues
                if (newsResponse.status != "ok" || newsResponse.articles.isEmpty()) {
                    shouldPagingHeadlineNews = false
                    _topHeadlineNewsProgress.postValue(Resource.Error("No Paging"))
                    return
                }

                fillFeatureArticleForTheFirstTime(articles)

                oldTopHeadlineArticles = oldTopHeadlineArticles.plus(articles) as ArrayList<Article>
                _topHeadlineNewsProgress.postValue(Resource.Success(oldTopHeadlineArticles))
                topHeadlineNewsPage++
                updateCacheStatues(TOP_HEADLINE_ARTICLES, oldTopHeadlineArticles)
            }
        else
            _topHeadlineNewsProgress.postValue(Resource.Error(response.message()))
    }

    //The following function will execute only once when we fetch data for the first time.
    // (To handle the featuredArticle when the database is empty)
    private fun fillFeatureArticleForTheFirstTime(articles: List<Article>) {
        if (_featuredArticle.value == null)
            _featuredArticle.postValue(articles.random())
    }


    private fun updateCacheStatues(
        articlesFlag: String,
        articles: List<Article>,
        category: String = ""
    ) =
        viewModelScope.launch {
            when (articlesFlag) {
                TOP_HEADLINE_ARTICLES -> {
                    articlesCache.updateTopHeadlineArticlesCache(articles)
                }
                CATEGORY_ARTICLES -> {
                    articlesCache.updateCategoryArticlesCache(category, articles)
                }
            }
        }

    private fun getNewsByCategory(category: String) = viewModelScope.launch {
        val articles = articlesRepository.getArticlesByCategory(category)
        _categoryNews.postValue(articles)
    }

    fun fetchNewsByCategory(category: String) = viewModelScope.launch {
        try {
            getNewsByCategory(category)
            if (shouldPagingCategoryNews) {
                _categoryNewsProgress.postValue(Resource.Loading())
                val response =
                    articlesRepository.fetchNewsByCategory("us", categoryNewsPage, category)
                handleCategoryNews(response, category)

            } else
                _categoryNewsProgress.postValue(Resource.Error("No Paging"))
        } catch (e: Exception) {
            _categoryNewsProgress.postValue(Resource.Error(e.message.toString()))
        }
    }

    private var oldCategoryArticles = ArrayList<Article>()
    private fun handleCategoryNews(response: Response<NewsResponse>, category: String) {
        if (response.isSuccessful)
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                if (articles.isEmpty() || newsResponse.status != "ok") {
                    shouldPagingCategoryNews = false
                    _categoryNewsProgress.postValue(Resource.Error("No Paging"))
                    return
                }

                //Handle the first time we open the category
                if(_categoryNews.value.isNullOrEmpty())
                    _categoryNews.postValue(articles)

                oldCategoryArticles = oldCategoryArticles.plus(articles) as ArrayList<Article>
                _categoryNewsProgress.postValue(Resource.Success(oldCategoryArticles))
                categoryNewsPage++
                updateCacheStatues(CATEGORY_ARTICLES, oldCategoryArticles, category)
            }
        else {
            _categoryNewsProgress.postValue(Resource.Error(response.message()))
        }
    }

}
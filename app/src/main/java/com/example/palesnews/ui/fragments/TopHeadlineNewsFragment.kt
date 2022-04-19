package com.example.palesnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.palesnews.adapters.ArticlesAdapter
import com.example.palesnews.adapters.CategoriesAdapter
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.palesnews.helper.ResourceResultHandler
import com.example.palesnews.util.Constants.Companion.BUSINESS
import com.example.palesnews.util.Constants.Companion.GENERAL
import com.example.palesnews.util.Constants.Companion.SINCE
import com.example.palesnews.util.Constants.Companion.SPORTS
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TopHeadlineNewsFragment : Fragment() {
    @Inject
    lateinit var glide: RequestManager
    val TAG = "TopHeadlineNewsFragment"
    private lateinit var binding: FragmentTopHeadlineNewsBinding
    private val viewModel by activityViewModels<NewsViewModel>()
    lateinit var topHeadlineResultHandler: ResourceResultHandler
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopHeadlineNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesRecyclerview()
        setupArticlesRecyclerview()


        topHeadlineResultHandler = ResourceResultHandler(
            onLoading = {
                Log.d(TAG, "TopHeadLines:Loading...")
                showLoading()
            },

            onSuccess = {
                Log.d(TAG, "TopHeadLines:Success :)")
                hideLoading()
            },

            onError = {
                Log.e(TAG, "TopHeadLines:Error :(")
                hideLoading()
            }
        )

        viewModel.topHeadlineNewsProgress.observe(viewLifecycleOwner) { result ->
            topHeadlineResultHandler.handleResult(result)
        }

        viewModel.topHeadlineNews.observe(viewLifecycleOwner) { articles ->
            articlesAdapter.differ.submitList(articles)
            // We will pick a random article and show it in the Featured Articles
            val featuredArticle = articles.random()
            setupFeaturedArticle(featuredArticle)
        }
    }

    private fun setupFeaturedArticle(featuredArticle: Article) {
        glide.load(featuredArticle.urlToImage).into(binding.imgFeaturedArticle)
        binding.tvOverImage.text = featuredArticle.title
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun setupArticlesRecyclerview() {
        articlesAdapter = ArticlesAdapter()
        binding.rvTopHeadlines.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = articlesAdapter
        }
    }

    private fun setupCategoriesRecyclerview() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
        }
        val categoriesList = getCategoriesList()
        categoriesAdapter.setCategoriesList(categoriesList)
    }

    private fun getCategoriesList(): MutableList<String> {
        return mutableListOf(
            GENERAL,
            BUSINESS,
            SINCE,
            SPORTS
        )

    }
}
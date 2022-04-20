package com.example.palesnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.palesnews.R
import com.example.palesnews.adapters.ArticlesAdapter
import com.example.palesnews.adapters.CategoriesAdapter
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.palesnews.helper.VerticalRecyclerViewDecoration
import com.example.palesnews.helper.ResourceResultHandler
import com.example.palesnews.util.Constants.Companion.BUSINESS
import com.example.palesnews.util.Constants.Companion.GENERAL
import com.example.palesnews.util.Constants.Companion.SINCE
import com.example.palesnews.util.Constants.Companion.SPORTS
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

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
                hidePagingLoading()

            },

            onError = {
                Log.e(TAG, "TopHeadLines:Error :( ${it.toString()}")
                hideLoading()
                hidePagingLoading()

            }
        )

        viewModel.topHeadlineNewsProgress.observe(viewLifecycleOwner) { result ->
            topHeadlineResultHandler.handleResult(result)
        }

        viewModel.topHeadlineNews.observe(viewLifecycleOwner) { articles ->
            articlesAdapter.differ.submitList(articles)
        }

        var featuredArticle: Article? = null
        viewModel.featuredArticle.observe(viewLifecycleOwner) { featuredArticleResult ->
            setupFeaturedArticle(featuredArticleResult)
            featuredArticle = featuredArticleResult

        }

        //Paging
        binding.nestedScrollViewHeadline.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (v.getChildAt(0).bottom <= (v.height + scrollY))
                    viewModel.fetchTopHeadlineNews("us").also { showPagingLoading() }
            }
        )

        binding.imgFeaturedArticle.setOnClickListener {
            val readingTime = Random.nextInt(1, 5).toString()
            val time = "$readingTime ${requireContext().getString(R.string.read_time)} | ${featuredArticle?.publishedAt}"
            navigateTo(R.id.action_topHeadlineNewsFragment_to_articleFragment, featuredArticle,time)
        }

        articlesAdapter.onItemClick = { article, time ->
            navigateTo(R.id.action_topHeadlineNewsFragment_to_articleFragment,article,time)
        }

    }

    private fun navigateTo(actionId: Int, article: Article?,time:String) = article?.let {
        val bundle = Bundle().apply {
            putParcelable("article", it)
            putString("time", time)
        }
        findNavController().navigate(actionId, bundle)
    }

    private fun showPagingLoading() {
        binding.progressbarHeadlineNews.visibility = View.VISIBLE
    }

    private fun hidePagingLoading() {
        binding.progressbarHeadlineNews.visibility = View.GONE
    }

    private fun setupFeaturedArticle(featuredArticle: Article) {
        glide.load(featuredArticle.urlToImage).into(binding.imgFeaturedArticle)
        binding.tvFeaturedArticleTitle.text = featuredArticle.title
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
            addItemDecoration(VerticalRecyclerViewDecoration(60))
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
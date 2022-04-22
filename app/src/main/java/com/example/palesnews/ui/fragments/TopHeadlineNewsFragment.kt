package com.example.palesnews.ui.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.palesnews.data.pojo.Country
import com.example.palesnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.palesnews.helper.Navigation
import com.example.palesnews.helper.VerticalRecyclerViewDecoration
import com.example.palesnews.helper.ResourceResultHandler
import com.example.palesnews.ui.dialogs.CountryPickerDialog
import com.example.palesnews.util.Constants.Companion.BUSINESS
import com.example.palesnews.util.Constants.Companion.COUNTRY_CODE
import com.example.palesnews.util.Constants.Companion.EGYPT
import com.example.palesnews.util.Constants.Companion.ENTERTAINMENT
import com.example.palesnews.util.Constants.Companion.GERMANY
import com.example.palesnews.util.Constants.Companion.RUSSIA
import com.example.palesnews.util.Constants.Companion.SINCE
import com.example.palesnews.util.Constants.Companion.SPORTS
import com.example.palesnews.util.Constants.Companion.USA
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TopHeadlineNewsFragment : Fragment() {
    val TAG = "TopHeadlineNewsFragment"

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var navigation: Navigation
    private val viewModel by activityViewModels<NewsViewModel>()

    private lateinit var binding: FragmentTopHeadlineNewsBinding
    private lateinit var topHeadlinesProgressHandler: ResourceResultHandler
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesRecyclerview()
        setupArticlesRecyclerview()

        setCountryFlag()


        topHeadlinesProgressHandler = ResourceResultHandler(
            onLoading = {
                Log.d(TAG, "TopHeadLines:Loading...")
                showLoading()
            },

            onSuccess = { articlesList ->
                Log.d(TAG, "TopHeadLines:Success :) ${articlesList.size}")
                hideLoading()
                hidePagingLoading()
            },

            onError = { error ->
                Log.e(TAG, "TopHeadLines:Error :( $error")
                hideLoading()
                hidePagingLoading()
            }
        )


        viewModel.topHeadlineNewsProgress.observe(viewLifecycleOwner) { result ->
            topHeadlinesProgressHandler.handleResult(result)
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


        binding.tvSearchBox.setOnClickListener {
            navigation.navigateTo(
                findNavController(),
                R.id.action_topHeadlineNewsFragment_to_searchNewsFragment
            )
        }

        binding.imgFeaturedArticle.setOnClickListener {
            //Simulate a random number for the reading time based on content
            val readingTime = featuredArticle?.content?.length?.div(70)
            val time =
                "$readingTime ${requireContext().getString(R.string.read_time)} | ${featuredArticle?.publishedAt}"
            navigation.navigateTo(
                findNavController(),
                R.id.action_topHeadlineNewsFragment_to_articleFragment,
                featuredArticle,
                time
            )
        }

        binding.imgCountry.setOnClickListener {
            val countriesList = getCountriesList()
            val countryPicker = CountryPickerDialog(countriesList)
            countryPicker.showDialog(requireContext(),
                onClick = { country ->
                    saveNewCountry(country)
                    binding.imgCountry.setImageResource(country.image)
                    viewModel.refreshTopHeadlineNews(country.countryCode)
                    viewModel.refreshFeaturedArticle()
                })
        }

        articlesAdapter.onItemClick = { article, time ->
            navigation.navigateTo(
                findNavController(),
                R.id.action_topHeadlineNewsFragment_to_articleFragment,
                article,
                time
            )
        }

        categoriesAdapter.onItemClick = { category ->
            navigation.navigateTo(
                findNavController(),
                R.id.action_topHeadlineNewsFragment_to_categoryArticlesFragment,
                category
            )
        }



        binding.refreshTopHeadlineNews.setOnRefreshListener {
            val countryCode = getCountryCode()
            viewModel.refreshTopHeadlineNews(countryCode)
            viewModel.refreshFeaturedArticle()
        }


    }

    private fun getCountryCode(): String {
        return requireActivity().getSharedPreferences(COUNTRY_CODE, MODE_PRIVATE)
            .getString(COUNTRY_CODE, USA) ?: USA
    }

    private fun saveNewCountry(country: Country) {
        requireActivity().getSharedPreferences(COUNTRY_CODE, MODE_PRIVATE).edit().apply {
            putString(COUNTRY_CODE, country.countryCode)
        }.apply()
    }

    private fun getCountriesList(): List<Country> {
        val countryCode =
            requireActivity().getSharedPreferences(COUNTRY_CODE, MODE_PRIVATE).getString(
                COUNTRY_CODE, USA
            )

        val usa = Country(getString(R.string.usa), USA, R.drawable.usa, false)
        val germany = Country(getString(R.string.germany), GERMANY, R.drawable.germany, false)
        val russia = Country(getString(R.string.russia), RUSSIA, R.drawable.russia, false)
        val egypt = Country(getString(R.string.egypt), EGYPT, R.drawable.egypt, false)

        when (countryCode) {
            USA -> {
                usa.isPicked = true
            }
            GERMANY -> {
                germany.isPicked = true
            }

            RUSSIA -> {
                russia.isPicked = true
            }

            EGYPT -> {
                egypt.isPicked = true
            }
        }

        val countriesList = ArrayList<Country>().apply {
            add(usa)
            add(germany)
            add(russia)
            add(egypt)
        }

        return countriesList
    }

    private fun setCountryFlag() {
        val countryCode =
            requireActivity().getSharedPreferences(COUNTRY_CODE, MODE_PRIVATE).getString(
                COUNTRY_CODE, USA
            )
        when (countryCode) {
            USA -> {
                binding.imgCountry.setImageResource(R.drawable.usa)
            }
            GERMANY -> {
                binding.imgCountry.setImageResource(R.drawable.germany)
            }

            RUSSIA -> {
                binding.imgCountry.setImageResource(R.drawable.russia)
            }

            EGYPT -> {
                binding.imgCountry.setImageResource(R.drawable.egypt)
            }
        }
    }


    private fun showPagingLoading() {
        binding.progressbarHeadlineNewsPaging.visibility = View.VISIBLE
    }

    private fun hidePagingLoading() {
        binding.progressbarHeadlineNewsPaging.visibility = View.GONE
        binding.refreshTopHeadlineNews.isRefreshing = false
    }

    private fun setupFeaturedArticle(featuredArticle: Article) {
        glide.load(featuredArticle.urlToImage).into(binding.imgFeaturedArticle)
        binding.tvFeaturedArticleTitle.text = featuredArticle.title
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
        binding.refreshTopHeadlineNews.isRefreshing = false
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
            ENTERTAINMENT,
            BUSINESS,
            SINCE,
            SPORTS
        )
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("test","Destroyed")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.d("test","Stopped")
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d("test","Create")
//    }
}

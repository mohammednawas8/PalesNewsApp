package com.example.palesnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palesnews.R
import com.example.palesnews.adapters.ArticlesAdapter
import com.example.palesnews.databinding.FragmentCategoryArticlesBinding
import com.example.palesnews.helper.Navigation
import com.example.palesnews.helper.ResourceResultHandler
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryArticlesFragment : Fragment() {
    @Inject
    lateinit var navigation: Navigation
    val TAG = "CategoryArticlesFragmen"
    private lateinit var categoryArticlesHandler: ResourceResultHandler
    private lateinit var binding: FragmentCategoryArticlesBinding
    private lateinit var articlesAdapter: ArticlesAdapter
    val viewModel by viewModels<NewsViewModel>()
    val args by navArgs<CategoryArticlesFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryArticlesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = args.category

        setupRecyclerView()
        binding.tvCategory.text = category
        viewModel.fetchNewsByCategory(category)

        categoryArticlesHandler = ResourceResultHandler(
            onLoading = {
                Log.d(TAG, "CategoryArticles: Loading ...")
                showLoading()
            },
            onSuccess = { articles ->
                Log.d(TAG, "CategoryArticles: Success :) ${articles.size}")

                hideLoading()
            },
            onError = { message ->
                Log.e(TAG, "CategoryArticles: Error :( $message")
                hideLoading()
            }
        )
        viewModel.categoryNewsProgress.observe(viewLifecycleOwner) { result ->
            categoryArticlesHandler.handleResult(result)
        }

        viewModel.categoryNews.observe(viewLifecycleOwner) { articles ->
            articlesAdapter.differ.submitList(articles)
        }

        articlesAdapter.onItemClick = { article, time ->
            navigation.navigateTo(
                findNavController(),
                R.id.action_categoryArticlesFragment_to_articleFragment,
                article,
                time
            )
        }

        //Paging
        binding.rvCategoryArticles.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!recyclerView.canScrollVertically(1) && dy != 0) {
                        viewModel.fetchNewsByCategory(category)
                        showLoading()
                    }
                }
            }
        )

    }



    private fun hideLoading() {
        binding.progressbarCategoryNews.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbarCategoryNews.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        articlesAdapter = ArticlesAdapter()
        binding.rvCategoryArticles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Reset page number
        viewModel.categoryNewsPage = 1
    }
}
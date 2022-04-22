package com.example.palesnews.ui.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palesnews.R
import com.example.palesnews.adapters.ArticlesAdapter
import com.example.palesnews.databinding.FragmentSearchBinding
import com.example.palesnews.helper.Navigation
import com.example.palesnews.helper.ResourceResultHandler
import com.example.palesnews.helper.VerticalRecyclerViewDecoration
import com.example.palesnews.util.Constants
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchNewsFragment : Fragment() {
    @Inject
    lateinit var navigation: Navigation
    val TAG = "SearchNewsFragment"
    private lateinit var searchResultHandler: ResourceResultHandler
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: ArticlesAdapter
    val viewModel by viewModels<NewsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()

        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.edSearchBox.requestFocus()
        binding.edSearchBox.isCursorVisible = true

        searchResultHandler = ResourceResultHandler(
            onLoading = {
                Log.d(TAG, "SearchArticles Loading ...")
            },
            onSuccess = { searchArticles ->
                searchAdapter.differ.submitList(searchArticles)
                Log.d(TAG, "SearchArticles Success :) ${searchArticles.size}")
            },
            onError = { message ->
                Log.e(TAG, "SearchArticles Error :( $message")
            }
        )


        viewModel.searchNews.observe(viewLifecycleOwner) { result ->
            searchResultHandler.handleResult(result)
        }

        searchAdapter.onItemClick = { article, time ->
            navigation.navigateTo(
                findNavController(),
                R.id.action_searchNewsFragment_to_articleFragment,
                article,
                time
            )
        }

        //Searching
        var job: Job? = null
        binding.edSearchBox.addTextChangedListener { searchQuery ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_DELAY)
                viewModel.searchNews(searchQuery.toString())
            }
        }

    }


    private fun setupRecyclerView() {
        searchAdapter = ArticlesAdapter()
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
            addItemDecoration(VerticalRecyclerViewDecoration(60))
        }
    }

}
package com.example.palesnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.palesnews.data.database.ArticlesDatabase
import com.example.palesnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.palesnews.helper.ResourceResultHandler
import com.example.palesnews.repositories.ArticlesRepository
import com.example.palesnews.util.Resource
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TopHeadlineNewsFragment : Fragment() {
    val TAG = "TopHeadlineNewsFragment"
    private lateinit var binding: FragmentTopHeadlineNewsBinding
    private val viewModel by activityViewModels<NewsViewModel>()
    lateinit var topHeadlineResultHandler: ResourceResultHandler

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

        viewModel.fetchTopHeadlineNews("us", 1)

        topHeadlineResultHandler = ResourceResultHandler(
            onLoading = {
                Log.d(TAG, "Loading...")
            },

            onSuccess = {
                Log.d(TAG, "Success :)")
            },

            onError = {
                Log.e(TAG, "Error :(")
            }
        )

        viewModel.topHeadlineNewsProgress.observe(viewLifecycleOwner) { result ->
            topHeadlineResultHandler.handleResult(result)
        }

        viewModel.topHeadlineNews.observe(viewLifecycleOwner) { articles ->
        }
    }
}
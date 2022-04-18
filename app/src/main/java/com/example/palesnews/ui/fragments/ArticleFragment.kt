package com.example.palesnews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.palesnews.databinding.FragmentArticleBinding
import com.example.palesnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.palesnews.viewModels.NewsViewModel

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    val viewModel by viewModels<NewsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater,container,false)
        return binding.root
    }

}
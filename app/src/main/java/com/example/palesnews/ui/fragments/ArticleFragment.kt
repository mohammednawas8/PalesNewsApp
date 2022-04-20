package com.example.palesnews.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.example.palesnews.R
import com.example.palesnews.databinding.FragmentArticleBinding
import com.example.palesnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.palesnews.viewModels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArticleFragment : Fragment() {
    @Inject
    lateinit var glide: RequestManager
    private lateinit var binding: FragmentArticleBinding
    val viewModel by viewModels<NewsViewModel>()
    val args by navArgs<ArticleFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article
        val time = args.time
        binding.apply {
            glide.load(article.urlToImage).into(imgArticle)
            tvArticleTitle.text = article.title
            tvFirstLetter.text = article.title?.get(0).toString()
            tvArticleTime.text = time
            tvArticleContent.text = article.content?.plus(getString(R.string.read_more))
        }

        binding.imgWebview.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            startActivity(intent)
        }
    }

}
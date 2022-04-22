package com.example.palesnews.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.palesnews.R
import com.example.palesnews.data.pojo.Article
import com.example.palesnews.databinding.ArticleItemBinding
import javax.inject.Inject
import kotlin.random.Random

class ArticlesAdapter() : RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder>() {

    inner class ArticlesViewHolder(
        private val binding: ArticleItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(article: Article) {
            //Simulate a random number for the reading time based on content
            val readingTime = article.content?.length?.div(70)
            binding.apply {
                Glide.with(context)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error_loading)
                    .into(imgArticle)
                tvArticleTitle.text = article.title
                tvArticleTime.text =
                    "$readingTime ${context.getString(R.string.read_time)} | ${article.publishedAt}"
            }
        }

        fun getTime() : String = binding.tvArticleTime.text.toString()
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        return ArticlesViewHolder(
            ArticleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(article,holder.getTime())
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Article, String) -> Unit)? = null
}
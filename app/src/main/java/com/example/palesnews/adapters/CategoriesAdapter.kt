package com.example.palesnews.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.palesnews.R
import com.example.palesnews.databinding.CategoryItemBinding
import com.example.palesnews.util.Constants.Companion.BUSINESS
import com.example.palesnews.util.Constants.Companion.GENERAL
import com.example.palesnews.util.Constants.Companion.SINCE
import com.example.palesnews.util.Constants.Companion.SPORTS
import javax.inject.Inject

class CategoriesAdapter() : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {


    var categoryList = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setCategoriesList(categoryList: List<String>) {
        this.categoryList = categoryList.toMutableList()
        notifyDataSetChanged()
    }

    inner class CategoriesViewHolder(
        val binding: CategoryItemBinding,
        val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            when (category) {
                BUSINESS -> {
                    changeButtonTextAndColor(
                        binding.btnCategory,
                        context.getText(R.string.business),
                        context.resources.getColor(R.color.g_red)
                    )
                    return
                }

                GENERAL -> {
                    changeButtonTextAndColor(
                        binding.btnCategory,
                        context.getText(R.string.general),
                        context.resources.getColor(R.color.g_orange)
                    )
                    return
                }

                SINCE -> {
                    changeButtonTextAndColor(
                        binding.btnCategory,
                        context.getText(R.string.since),
                        context.resources.getColor(R.color.g_blue)
                    )
                    return
                }

                SPORTS -> {
                    changeButtonTextAndColor(
                        binding.btnCategory,
                        context.getText(R.string.sports),
                        context.resources.getColor(R.color.g_green)
                    )
                    return
                }
            }
        }
    }

    private fun changeButtonTextAndColor(
        btnCategory: AppCompatButton,
        name: CharSequence,
        color: Int
    ) {
        btnCategory.text = name
        btnCategory.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context)
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)

        holder.binding.btnCategory.setOnClickListener {
            onItemClick?.invoke(category)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    var onItemClick: ((String) -> Unit)? = null
}
package com.example.palesnews.helper

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.palesnews.data.pojo.Article

class Navigation {

    fun navigateTo(
        navController: NavController,
        destinationId: Int,
        article: Article?,
        time: String
    ) = article?.let {
        val bundle = Bundle().apply {
            putParcelable("article", it)
            putString("time", time)
        }
        navController.navigate(destinationId, bundle)
    }

    fun navigateTo(
        navController: NavController,
        destinationId: Int,
        category: String
    ) {
        val bundle = Bundle().apply {
            putString("category", category)
        }
        navController.navigate(destinationId, bundle)
    }

    fun navigateTo(
        navController: NavController,
        destinationId: Int,
    ) {
        navController.navigate(destinationId)
    }
}



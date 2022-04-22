package com.example.palesnews.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palesnews.R
import com.example.palesnews.adapters.CountryPickerAdapter
import com.example.palesnews.data.pojo.Country
import com.example.palesnews.helper.VerticalRecyclerViewDecoration
import javax.inject.Inject

class CountryPickerDialog(private val countriesList: List<Country>) {
    private lateinit var countryPickerAdapter: CountryPickerAdapter

    fun showDialog(context: Context,onClick:(Country) -> Unit) {
        val alertDialog = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.alertdialog_country_picker, null)
        setupView(view)
        alertDialog.setView(view)
        alertDialog.show()

        countryPickerAdapter.onItemClick = { country ->
            onClick(country)
            alertDialog.dismiss()
        }
    }


    private fun setupView(view: View) {
        countryPickerAdapter = CountryPickerAdapter()
        val rv = view.findViewById<RecyclerView>(R.id.rv_country_picker)
        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countryPickerAdapter
            addItemDecoration(VerticalRecyclerViewDecoration(25))
        }
        countryPickerAdapter.setCountries(countriesList)
    }
}
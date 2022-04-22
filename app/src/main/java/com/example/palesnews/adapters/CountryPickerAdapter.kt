package com.example.palesnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.palesnews.data.pojo.Country
import com.example.palesnews.databinding.CountriesPickerBinding

class CountryPickerAdapter : RecyclerView.Adapter<CountryPickerAdapter.CountryPickerViewHolder>() {
    private var countriesList = ArrayList<Country>()
    fun setCountries(countriesList : List<Country>){
        this.countriesList = countriesList as ArrayList<Country>
        notifyDataSetChanged()
    }

    inner class CountryPickerViewHolder(private val binding: CountriesPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(country:Country){
                binding.apply {
                    imgCountry.setImageResource(country.image)
                    tvCountry.text = country.name
                    if(country.isPicked)
                        imgPicked.visibility = View.VISIBLE
                    else
                        imgPicked.visibility = View.INVISIBLE
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryPickerViewHolder {
        return CountryPickerViewHolder(
            CountriesPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryPickerViewHolder, position: Int) {
        val country = countriesList[position]
        holder.bind(country)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(country)
        }
    }

    override fun getItemCount(): Int {
        return countriesList.size
    }

    var onItemClick : ((Country)->Unit)?=null
}
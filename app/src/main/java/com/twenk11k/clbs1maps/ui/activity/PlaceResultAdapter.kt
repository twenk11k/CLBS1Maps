package com.twenk11k.clbs1maps.ui.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.twenk11k.clbs1maps.R
import com.twenk11k.clbs1maps.databinding.AdapterPlaceResultBinding
import com.twenk11k.clbs1maps.model.PlaceResult

class PlaceResultAdapter(private val context: Context,
                         var listPlaceResult: MutableList<PlaceResult>
                         ): RecyclerView.Adapter<PlaceResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceResultAdapter.ViewHolder {
        val binding: AdapterPlaceResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_place_result,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listPlaceResult.size
    }

    override fun onBindViewHolder(holder: PlaceResultAdapter.ViewHolder, position: Int) {
        holder.bind(listPlaceResult[holder.adapterPosition])
    }

    inner class ViewHolder(val binding: AdapterPlaceResultBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(placeResult: PlaceResult) {
            binding.textPlaceName.text = placeResult.placeName
            binding.textLat.text = placeResult.geometry.location.lat.toString()
            binding.textLng.text = placeResult.geometry.location.lng.toString()
            val distanceText = "${placeResult.geometry.location.distance} ${context.getString(R.string.km)}"
            binding.textDistance.text = distanceText
        }

    }

}
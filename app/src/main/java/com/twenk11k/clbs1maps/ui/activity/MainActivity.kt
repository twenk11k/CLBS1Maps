package com.twenk11k.clbs1maps.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.twenk11k.clbs1maps.R
import com.twenk11k.clbs1maps.databinding.ActivityMainBinding
import com.twenk11k.clbs1maps.ui.util.Utils.Companion.getZoomLevel


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var buttonSearch: Button

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setViews()
        setMaps()
    }

    private fun setViews() {
        buttonSearch = binding.buttonSearch
        handleButtonSearchListener()
    }

    private fun handleButtonSearchListener() {
        buttonSearch.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.please_enter_details)
                customView(R.layout.dialog_search)
                positiveButton(R.string.search) {
                    val customView = getCustomView()
                    val textLatitude =
                        customView.findViewById<TextInputEditText>(R.id.edit_latitude).text.toString()
                    val textLongitude =
                        customView.findViewById<TextInputEditText>(R.id.edit_longitude).text.toString()
                    val textRadius =
                        customView.findViewById<TextInputEditText>(R.id.edit_radius).text.toString()

                    val latitude: Double =
                        if (textLatitude.isNotEmpty()) textLatitude.toDouble() else 0.0
                    val longitude: Double =
                        if (textLongitude.isNotEmpty()) textLongitude.toDouble() else 0.0
                    val radius: Double =
                        if (textRadius.isNotEmpty()) textRadius.toDouble() else 0.0

                }
                negativeButton(text = "Close")
            }
        }
    }

    private fun setMaps() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val chiangMai = LatLng(18.7717874, 98.9742796)

        val circleOptions = CircleOptions()
            .center(chiangMai)
            .radius(500.0)
            .strokeWidth(0f)

        val circle = map.addCircle(circleOptions)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                circleOptions.center,
                getZoomLevel(circle)
            )
        )
    }

}
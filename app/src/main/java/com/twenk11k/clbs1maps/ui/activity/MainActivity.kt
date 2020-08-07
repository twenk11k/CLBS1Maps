package com.twenk11k.clbs1maps.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.twenk11k.clbs1maps.R
import com.twenk11k.clbs1maps.databinding.ActivityMainBinding
import com.twenk11k.clbs1maps.ui.util.Utils.Companion.getZoomLevel


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setMaps()
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
package com.twenk11k.clbs1maps.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.textfield.TextInputEditText
import com.twenk11k.clbs1maps.R
import com.twenk11k.clbs1maps.common.spinnermap.OnSpinnerItemSelectedListener
import com.twenk11k.clbs1maps.common.spinnermap.SpinnerMap
import com.twenk11k.clbs1maps.databinding.ActivityMainBinding
import com.twenk11k.clbs1maps.extensions.gone
import com.twenk11k.clbs1maps.extensions.visible
import com.twenk11k.clbs1maps.model.PlaceResult
import com.twenk11k.clbs1maps.ui.util.Utils.Companion.getZoomLevel
import com.twenk11k.clbs1maps.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : DataBindingActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var buttonSearch: Button
    private lateinit var spinnerMap: SpinnerMap
    private lateinit var mapConstraintLayout: View

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViews()
        setMaps()
    }

    private fun setViews() {
        buttonSearch = binding.buttonSearch
        spinnerMap = binding.spinnerMap
        mapConstraintLayout = binding.mapConstraintLayout
        handleButtonSearchListener()
        initializeSpinnerMap()
    }

    private fun initializeSpinnerMap() {
        val list = LinkedList(
            listOf(
                getString(R.string.display_on_maps),
                getString(R.string.display_on_list)
            )
        )
        spinnerMap.attachDataSource(list)
        spinnerMap.setOnSpinnerItemSelectedListener(object : OnSpinnerItemSelectedListener {
            override fun onItemSelected(parent: SpinnerMap?, view: View?, position: Int, id: Long) {
                if (position == 0)
                    mapConstraintLayout.gone()
                else
                    mapConstraintLayout.visible()

            }
        })
    }

    private fun handleButtonSearchListener() {
        buttonSearch.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.please_enter_details)
                customView(R.layout.dialog_search)
                negativeButton(R.string.close)
                positiveButton(R.string.search) {
                    val customView = getCustomView()
                    val textLatitude =
                        customView.findViewById<TextInputEditText>(R.id.edit_latitude).text.toString()
                    val textLongitude =
                        customView.findViewById<TextInputEditText>(R.id.edit_longitude).text.toString()
                    val textRadius =
                        customView.findViewById<TextInputEditText>(R.id.edit_radius).text.toString()

                    val lat: Double =
                        if (textLatitude.isNotEmpty()) textLatitude.toDouble() else 0.0
                    val lng: Double =
                        if (textLongitude.isNotEmpty()) textLongitude.toDouble() else 0.0
                    val radius: Double =
                        if (textRadius.isNotEmpty()) textRadius.toDouble() else 0.0

                    val testlat = 18.7717874
                    val testlng = 98.9742796
                    val testrad = 1000.0
                    handleOperation(lat, lng, testrad)

                }
            }
        }
    }

    private fun handleOperation(lat: Double, lng: Double, radius: Double) {
        lifecycleScope.launch {
            viewModel.handleOperation(lat, lng, radius).observe(this@MainActivity, Observer {
                if (it?.isNotEmpty()!!)
                    updateMap(it, lat, lng, radius)
            })
        }
    }

    private fun updateMap(listEntry: List<PlaceResult>, lat: Double, lng: Double, radius: Double) {

        map.clear()

        val location = LatLng(lat, lng)

        map.addMarker(
            MarkerOptions()
                .position(location)
                .snippet("lat: $lat" + "\n" + "lng: $lng" + "\n" + "radius: $radius")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        )


        for (entry in listEntry) {
            val location1 = LatLng(entry.geometry.location.lat, entry.geometry.location.lng)
            map.addMarker(
                MarkerOptions()
                    .position(location1)
                    .snippet("Place Name: ${entry.placeName}" + "\n" + "lat: ${entry.geometry.location.lat}" + "\n" + "long: ${entry.geometry.location.lng}" + "\n" + "distance: ${entry.geometry.location.distance} km")
            )

        }

        val circleOptions = CircleOptions()
            .center(location)
            .radius(radius)
            .strokeWidth(0f)

        val circle = map.addCircle(circleOptions)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                circleOptions.center,
                getZoomLevel(circle)
            )
        )

    }

    private fun setMaps() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isMapToolbarEnabled = false

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
        setMapInfoViewAdapter()
    }

    private fun setMapInfoViewAdapter() {
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(baseContext)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(baseContext)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(baseContext)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
    }

}
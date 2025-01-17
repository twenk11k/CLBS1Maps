package com.twenk11k.clbs1maps.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.twenk11k.clbs1maps.R
import com.twenk11k.clbs1maps.common.spinnermap.OnSpinnerItemSelectedListener
import com.twenk11k.clbs1maps.common.spinnermap.SpinnerMap
import com.twenk11k.clbs1maps.databinding.ActivityMainBinding
import com.twenk11k.clbs1maps.extension.gone
import com.twenk11k.clbs1maps.extension.visible
import com.twenk11k.clbs1maps.model.PlaceResult
import com.twenk11k.clbs1maps.ui.viewmodel.MainViewModel
import com.twenk11k.clbs1maps.util.Utils.Companion.convertMeterToKilometer
import com.twenk11k.clbs1maps.util.Utils.Companion.getZoomLevel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.map_constraint_layout.view.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : DataBindingActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var buttonSearch: Button
    private lateinit var spinnerMap: SpinnerMap
    private lateinit var mapConstraintLayout: View
    private lateinit var mapRecyclerView: RecyclerView

    private var currentLocation = LatLng(18.7717874, 98.9742796)
    private var currentRadius = 500.0

    private var listPlaceResult: MutableList<PlaceResult> = ArrayList()
    private lateinit var adapterPlaceResult: PlaceResultAdapter

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViews()
        setMaps()
    }

    private fun setViews() {
        buttonSearch = binding.buttonSearch
        spinnerMap = binding.spinnerMap
        mapConstraintLayout = binding.mapConstraintLayout
        mapRecyclerView = mapConstraintLayout.mapRecyclerView
        handleButtonSearchListener()
        initializeSpinnerMap()
        setAdapter()
        setRecyclerView()
    }

    private fun setAdapter() {
        adapterPlaceResult = PlaceResultAdapter(this, listPlaceResult)
    }

    private fun setRecyclerView() {
        mapRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        mapRecyclerView.adapter = adapterPlaceResult
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
                if (position == 0) {
                    mapConstraintLayout.gone()
                    zoomCamera()
                } else if (listPlaceResult.isNotEmpty()) {
                    mapConstraintLayout.visible()
                }
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
                        customView.findViewById<EditText>(R.id.edit_latitude).text.toString()
                    val textLongitude =
                        customView.findViewById<EditText>(R.id.edit_longitude).text.toString()
                    val textRadius =
                        customView.findViewById<EditText>(R.id.edit_radius).text.toString()

                    val lat: Double =
                        if (textLatitude.isNotEmpty()) textLatitude.toDouble() else 0.0
                    val lng: Double =
                        if (textLongitude.isNotEmpty()) textLongitude.toDouble() else 0.0
                    val radius: Double =
                        if (textRadius.isNotEmpty()) textRadius.toDouble() else 0.0

                    handleUserInput(lat, lng, radius)

                }
            }
        }
    }

    private fun handleUserInput(lat: Double, lng: Double, radius: Double) {
        lifecycleScope.launch {
            viewModel.handleUserInput(lat, lng, radius).observe(this@MainActivity, Observer {
                if (it?.isNotEmpty()!!) {
                    updateMap(it, lat, lng, radius)
                    updateAdapter(it)
                }
            })
        }
    }

    private fun updateAdapter(it: List<PlaceResult>) {
        if (spinnerMap.getSelectedIndex() == 1)
            mapConstraintLayout.visible()
        listPlaceResult.clear()
        listPlaceResult.addAll(it)
        adapterPlaceResult.notifyDataSetChanged()
    }

    private fun updateMap(listEntry: List<PlaceResult>, lat: Double, lng: Double, radius: Double) {

        map.clear()

        val location = LatLng(lat, lng)

        val radiusAsKm = convertMeterToKilometer(radius)

        map.addMarker(
            MarkerOptions()
                .position(location)
                .snippet("Lat: $lat\nLng: $lng\nRadius: $radiusAsKm km")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        )

        for (entry in listEntry) {
            val locationEntry = LatLng(entry.geometry.location.lat, entry.geometry.location.lng)
            map.addMarker(
                MarkerOptions()
                    .position(locationEntry)
                    .snippet(
                        "Place Name: ${entry.placeName}" + "\n" + "Lat: ${entry.geometry.location.lat}" + "\n" + "Lng: ${entry.geometry.location.lng}" + "\n" + "Distance: ${entry.geometry.location.distance} ${getString(
                            R.string.km
                        )}"
                    )
            )
        }

        currentLocation = location
        currentRadius = radius

        zoomCamera()

    }

    private fun zoomCamera() {
        val circleOptions = CircleOptions()
            .center(currentLocation)
            .radius(currentRadius)
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

        val circleOptions = CircleOptions()
            .center(currentLocation)
            .radius(currentRadius)
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

    override fun onBackPressed() {
        if (spinnerMap.getSelectedIndex() == 1) {
            spinnerMap.setSelectedIndex(0)
            mapConstraintLayout.gone()
            zoomCamera()
        } else {
            super.onBackPressed()
        }
    }

}
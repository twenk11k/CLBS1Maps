package com.twenk11k.clbs1maps.ui.util

import android.location.Location
import com.google.android.gms.maps.model.Circle
import kotlin.math.ln

class Utils {

    companion object {

        fun getZoomLevel(circle: Circle?): Float {
            var zoomLevel = 11
            if (circle != null) {
                val radius = circle.radius + circle.radius / 2
                val scale = radius / 500
                zoomLevel = (16 - ln(scale) / ln(2.0)).toInt()
            }
            return zoomLevel.toFloat()
        }

        fun calculateDistance(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Double {
            val startPoint = Location("start_point")
            startPoint.latitude = startLat
            startPoint.longitude = startLng
            val endPoint = Location("end_point")
            endPoint.latitude = endLat
            endPoint.longitude = endLng

            val distance = (startPoint.distanceTo(endPoint).toDouble() / 1000)
            val distanceFormatted = String.format("%.2f", distance)
            return distanceFormatted.toDouble()
        }

    }

}
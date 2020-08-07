package com.twenk11k.clbs1maps.ui.util

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

    }

}
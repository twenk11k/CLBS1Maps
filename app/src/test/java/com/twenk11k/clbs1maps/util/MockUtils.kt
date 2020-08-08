package com.twenk11k.clbs1maps.util

import com.twenk11k.clbs1maps.model.PlaceResult

object MockUtils {

    fun mockPlaceResultList() = listOf(PlaceResult(
        18.781580,
        99.013867,
        500.0,
        "ChIJXW-7kH462jARZ0ObpXBi1Jg",
        "Chiang Mai",
        PlaceResult.PlaceGeometry(PlaceResult.PlaceGeometry.PlaceLocation(18.7883439,98.98530079999999))
    ))

}
package com.twenk11k.clbs1maps.network

import javax.inject.Inject

class MapsClient @Inject constructor(private val mapsService: MapsService) {

    suspend fun fetchPlaceResponse(location: String, radius: Double) =
        mapsService.fetchPlaceResponse(location, radius)

}
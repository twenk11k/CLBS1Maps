package com.twenk11k.clbs1maps.repository

import android.widget.Toast
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import com.twenk11k.clbs1maps.App
import com.twenk11k.clbs1maps.db.PlaceResultDao
import com.twenk11k.clbs1maps.network.MapsClient
import com.twenk11k.clbs1maps.ui.util.Utils.Companion.calculateDistance
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val mapsClient: MapsClient,
    private val placeResultDao: PlaceResultDao
) {

    suspend fun handleOperation(lat: Double, lng: Double, radius: Double) = flow {

        var placeResultList = placeResultDao.getPlaceResultList(lat, lng, radius)
        if (placeResultList.isEmpty()) {
            val location = "$lat,$lng"

            val response = mapsClient.fetchPlaceResponse(location, radius)
            response.suspendOnSuccess {
                data.whatIfNotNull { response ->
                    placeResultList = response.results
                    placeResultList.forEach { placeResult ->
                        placeResult.endLat = lat
                        placeResult.endLng = lng
                        placeResult.radius = radius
                        placeResult.geometry.location.distance = calculateDistance(
                            placeResult.geometry.location.lat,
                            placeResult.geometry.location.lng,
                            lat,
                            lng
                        )
                    }
                    placeResultDao.insertPlaceResultList(placeResultList)
                    emit(placeResultList)
                }
            }.onError {
                Toast.makeText(App.getContext(), message(), Toast.LENGTH_SHORT).show()
            }.onException {
                Toast.makeText(App.getContext(), message(), Toast.LENGTH_LONG).show()
            }
        } else {
            emit(placeResultList)
        }
    }

}
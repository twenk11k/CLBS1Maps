package com.twenk11k.clbs1maps.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "place_result")
@JsonClass(generateAdapter = true)
data class PlaceResult(
    var endLat: Double = 0.0,
    var endLng: Double = 0.0,
    var radius: Double = 0.0,
    @field:Json(name = "place_id") @PrimaryKey val placeId: String,
    @field:Json(name = "name") val placeName: String,
    @field:Json(name = "geometry") val geometry: PlaceGeometry
) {

    @JsonClass(generateAdapter = true)
    data class PlaceGeometry(
        @field:Json(name = "location") val location: PlaceLocation
    ) {

        @JsonClass(generateAdapter = true)
        data class PlaceLocation(
            @field:Json(name = "lat") val lat: Double,
            @field:Json(name = "lng") val lng: Double,
            var distance: Double = 0.0
        )

    }

}
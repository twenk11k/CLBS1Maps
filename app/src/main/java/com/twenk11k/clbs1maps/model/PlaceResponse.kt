package com.twenk11k.clbs1maps.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceResponse(
    @field:Json(name = "results") val results: List<PlaceResult>
)

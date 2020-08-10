package com.twenk11k.clbs1maps.network

import com.skydoves.sandwich.ApiResponse
import com.twenk11k.clbs1maps.model.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsService {

    @GET("json?key=YOUR_API_KEY")
    suspend fun fetchPlaceResponse(
        @Query("location") location: String,
        @Query("radius") radius: Double
    ): ApiResponse<PlaceResponse>

}
package com.twenk11k.clbs1maps.network

import com.skydoves.sandwich.ApiResponse
import com.twenk11k.clbs1maps.model.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsService {

    @GET("json?key=AIzaSyCDxtgGnzqRw8pKxuVDyE1HGkdGDRGnLVU")
    suspend fun fetchPlaceResponse(
        @Query("location") location: String = "18.7717874,98.9742796",
        @Query("radius") radius: Double = 0.0
    ): ApiResponse<PlaceResponse>

}
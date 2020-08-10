package com.twenk11k.clbs1maps.db.converters

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.twenk11k.clbs1maps.model.PlaceResult

open class PlaceLocationTypeConverter {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromString(value: String): PlaceResult.PlaceGeometry.PlaceLocation? {
        val adapter: JsonAdapter<PlaceResult.PlaceGeometry.PlaceLocation> = moshi.adapter(PlaceResult.PlaceGeometry.PlaceLocation::class.java)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromPlaceLocationType(type: PlaceResult.PlaceGeometry.PlaceLocation): String {
        val adapter: JsonAdapter<PlaceResult.PlaceGeometry.PlaceLocation> = moshi.adapter(PlaceResult.PlaceGeometry.PlaceLocation::class.java)
        return adapter.toJson(type)
    }

}
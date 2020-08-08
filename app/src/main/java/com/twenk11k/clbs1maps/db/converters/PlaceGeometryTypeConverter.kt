package com.twenk11k.clbs1maps.db.converters

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.twenk11k.clbs1maps.model.PlaceResult

open class PlaceGeometryTypeConverter {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromString(value: String): PlaceResult.PlaceGeometry? {
        val adapter: JsonAdapter<PlaceResult.PlaceGeometry> = moshi.adapter(PlaceResult.PlaceGeometry::class.java)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromPlaceGeometryType(type: PlaceResult.PlaceGeometry): String {
        val adapter: JsonAdapter<PlaceResult.PlaceGeometry> = moshi.adapter(PlaceResult.PlaceGeometry::class.java)
        return adapter.toJson(type)
    }

}
package com.twenk11k.clbs1maps.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.twenk11k.clbs1maps.db.converters.PlaceGeometryTypeConverter
import com.twenk11k.clbs1maps.db.converters.PlaceLocationTypeConverter
import com.twenk11k.clbs1maps.model.PlaceResult

@Database(entities = [PlaceResult::class], version = 1, exportSchema = false)
@TypeConverters(value = [PlaceGeometryTypeConverter::class, PlaceLocationTypeConverter::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun placeResultDao(): PlaceResultDao

}
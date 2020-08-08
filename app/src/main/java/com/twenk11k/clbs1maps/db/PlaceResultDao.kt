package com.twenk11k.clbs1maps.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.twenk11k.clbs1maps.model.PlaceResult

@Dao
interface PlaceResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceResultList(placeResultList: List<PlaceResult>)

    @Query("SELECT * FROM place_result WHERE endLat = :endLat AND endLng = :endLng AND radius = :radius")
    suspend fun getPlaceResultList(endLat: Double, endLng: Double, radius: Double): List<PlaceResult>

}
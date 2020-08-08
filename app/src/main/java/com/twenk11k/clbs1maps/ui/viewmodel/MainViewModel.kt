package com.twenk11k.clbs1maps.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.twenk11k.clbs1maps.model.PlaceResult
import com.twenk11k.clbs1maps.repository.MainRepository


class MainViewModel @ViewModelInject constructor(private val mainRepository: MainRepository) :
    LiveCoroutinesModel() {

    suspend fun handleOperation(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): LiveData<List<PlaceResult>> {
        return mainRepository.handleOperation(latitude, longitude, radius).asLiveData()
    }

}
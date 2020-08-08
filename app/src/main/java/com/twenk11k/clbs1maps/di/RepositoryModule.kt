package com.twenk11k.clbs1maps.di

import com.twenk11k.clbs1maps.db.PlaceResultDao
import com.twenk11k.clbs1maps.network.MapsClient
import com.twenk11k.clbs1maps.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideMainRepository(mapsClient: MapsClient, placeResultDao: PlaceResultDao): MainRepository {
        return MainRepository(mapsClient, placeResultDao)
    }

}
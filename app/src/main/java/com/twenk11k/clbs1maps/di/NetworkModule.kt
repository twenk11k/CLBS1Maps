package com.twenk11k.clbs1maps.di

import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import com.twenk11k.clbs1maps.network.HttpRequestInterceptor
import com.twenk11k.clbs1maps.network.MapsClient
import com.twenk11k.clbs1maps.network.MapsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpRequestInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideMapsService(retrofit: Retrofit): MapsService {
        return retrofit.create(MapsService::class.java)
    }

    @Provides
    @Singleton
    fun provideMapsClient(mapsService: MapsService): MapsClient {
        return MapsClient(mapsService)
    }

}
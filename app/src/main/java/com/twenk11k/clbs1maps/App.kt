package com.twenk11k.clbs1maps

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {

        lateinit var application: Application

        fun getContext(): Context {
            return application.applicationContext
        }

    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }

}
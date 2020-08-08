package com.twenk11k.clbs1maps.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
abstract class LocalAppDatabase {

    lateinit var appDatabase: AppDatabase

    @Before
    fun initAppDatabase() {
        appDatabase = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeAppDatabase() {
        appDatabase.close()
    }

}
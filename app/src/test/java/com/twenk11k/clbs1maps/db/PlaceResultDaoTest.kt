package com.twenk11k.clbs1maps.db

import com.twenk11k.clbs1maps.util.MockUtils.mockPlaceResultList
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class PlaceResultDaoTest: LocalAppDatabase() {

    private lateinit var placeResultDao: PlaceResultDao

    @Before
    fun init() {
        placeResultDao = appDatabase.placeResultDao()
    }

    @Test
    fun insertAndLoadPlaceResultTest() = runBlocking {

        val mockPlaceResultList = mockPlaceResultList()
        placeResultDao.insertPlaceResultList(mockPlaceResultList)

        val loadFromDatabase = placeResultDao.getPlaceResultList(18.781580,99.013867, 500.0)
        assertThat(loadFromDatabase.toString(), `is`(mockPlaceResultList.toString()))

    }

}
package com.twenk11k.clbs1maps.network

import com.nhaarman.mockitokotlin2.mock
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class MapsServiceTest: ApiAbstract<MapsService>() {

    private lateinit var service: MapsService
    private val client: MapsClient = mock()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @Before
    fun initService() {
        service = createService(MapsService::class.java)
    }

    @Throws(IOException::class)
    @Test
    fun fetchPlaceResponseTest() = runBlocking {
        enqueueResponse()
        val apiResponse = service.fetchPlaceResponse()
        val apiResponseBody = requireNotNull((apiResponse as ApiResponse.Success).data)
        mockWebServer.takeRequest()
        client.fetchPlaceResponse("18.7717874,98.9742796",400.0)
        assertThat(apiResponseBody.results[0].endLat, `is`(18.771787324))
        assertThat(apiResponseBody.results[0].endLng, `is`(98.974279232))
        assertThat(apiResponseBody.results[0].geometry.location.distance, `is`(399.3))
        Thread.sleep(10000)
    }


}
package com.openclassrooms.realestatemanager

import android.content.Context
import android.location.Geocoder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val address = "Cathédrale Notre-dame de Paris"
    private val lat = 48.85296820000001
    private val long = 2.3499021

    @Test
    fun checkIfConnexionToInternetExist() {
        Assert.assertTrue(Utils.hasInternetConnection(appContext))
    }

    @Test
    fun checkIfGPSIsActivated() {
        Assert.assertTrue(Utils.hasGPSOn(appContext))
    }

    @Test
    fun checkIfCoordinateForAddress() {
        val cord = Geocoder(appContext)
        val addresses = cord.getFromLocationName(address, 1)
        println(addresses[0].latitude)
        println(addresses[0].longitude)
        Assert.assertTrue(addresses[0].latitude == lat)
        Assert.assertTrue(addresses[0].longitude == long)
    }
}
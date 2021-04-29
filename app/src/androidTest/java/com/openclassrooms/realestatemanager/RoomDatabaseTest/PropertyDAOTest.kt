package com.openclassrooms.realestatemanager.RoomDatabaseTest

import android.content.Context
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.PropertyDao
import com.openclassrooms.realestatemanager.room.RealEstateDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class PropertyDAOTest {
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    lateinit var database: RealEstateDatabase
    lateinit var propertyDAO: PropertyDao

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(appContext, RealEstateDatabase::class.java)
                .allowMainThreadQueries()
                .setTransactionExecutor(Executors.newSingleThreadExecutor())
                .build()

        propertyDAO = database.propertyDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun writeAndGetProperty() = runBlocking {
        insertAllProperty()

        val propertyValuesExpected = createProperty1()
        val property: Property = propertyDAO.findById(111)
        Assert.assertEquals(property.id, propertyValuesExpected.id)
        Assert.assertEquals(property.type, propertyValuesExpected.type)
        Assert.assertEquals(property.room, propertyValuesExpected.room)
        Assert.assertEquals(property.agent, propertyValuesExpected.agent)
    }

    @Test
    fun updateAndGetProperty() = runBlocking {
        insertAllProperty()

        val property: Property = propertyDAO.findById(111)
        Assert.assertFalse(property.agent == "jeanne")
        property.agent = "jeanne"
        propertyDAO.updateProperty(property)
        val propertyUpdated: Property = propertyDAO.findById(111)
        Assert.assertTrue(propertyUpdated.agent == "jeanne")
    }


    @Test
    fun getAllProperty() = runBlocking {
        insertAllProperty()
        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            propertyDAO.getAllProperties().collect { properties ->
                assertThat(properties.size, equalTo(3))
                latch.countDown()
            }
        }
        latch.await()
        job.cancel()
    }

    private suspend fun insertAllProperty() {
        propertyDAO.insertProperty(createProperty1())
        propertyDAO.insertProperty(createProperty2())
        propertyDAO.insertProperty(createProperty3())
    }

    private fun createProperty1(): Property {
        return Property(111, "house", "100000", "125", "4", "2", "1", "description",
                null, null, null, "12 flower street", null, null, false,
                null, "14/05/2020", "jack", 48.8527288, 2.3505635)
    }

    private fun createProperty2(): Property {
        return Property(222, "house", "150000", "250", "6", "3", "2", "description",
                null, null, null, null, null, null, false,
                null, "18/05/2020", "john", 48.8527288, 2.3505635)
    }

    private fun createProperty3(): Property {
        return Property(333, "condo", "75000", "75", "2", "1", "1", "description",
                null, null, null, "135 downtown boulevard", null, null, false,
                null, "25/07/2020", "jean", 48.8527288, 2.3505635)
    }
}
package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider
import com.openclassrooms.realestatemanager.room.RealEstateDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentProviderTest {
    private lateinit var mContentResolver: ContentResolver
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        mContentResolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
    }

    //Get a property when it's not inserted before
    @Test
    fun getItemsWhenNoPropertyInserted() {
        val cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_PROPERTY, 123), null, null, null, null)
        Assert.assertNotNull(cursor)
        Assert.assertEquals(0, cursor?.count)
        cursor?.close()
    }


    @Test
    fun testIfContentProviderQueryWork() = runBlocking{
        val propertyExpect = createProperty1()
        //Insert a property to the database
        RealEstateDatabase.getDatabase(context).propertyDao().insertProperty(createProperty1())

        //Get this property with the contentProvider query with the id
        val cursorShouldBePopulate = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_PROPERTY, 123), null, null, null, null)

        //Check if the query work and get the good property, compare the result with the property expected
        Assert.assertNotNull(cursorShouldBePopulate)
        Assert.assertEquals(1, cursorShouldBePopulate?.count)
        Assert.assertTrue(cursorShouldBePopulate!!.moveToFirst())
        Assert.assertEquals(propertyExpect.address, cursorShouldBePopulate.getString(cursorShouldBePopulate.getColumnIndexOrThrow("property_address")))
        Assert.assertEquals(propertyExpect.agent, cursorShouldBePopulate.getString(cursorShouldBePopulate.getColumnIndexOrThrow("property_agent")))
        Assert.assertEquals(propertyExpect.type, cursorShouldBePopulate.getString(cursorShouldBePopulate.getColumnIndexOrThrow("property_type")))
        Assert.assertEquals(propertyExpect.arrivalDate, cursorShouldBePopulate.getString(cursorShouldBePopulate.getColumnIndexOrThrow("property_arrival_date")))

        //Delete the property from the database
        RealEstateDatabase.getDatabase(context).propertyDao().deletePropertyById(123)
        //Get the property with the contentProvider query with the id
        val cursorShouldBeEmpty = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_PROPERTY, 123), null, null, null, null)
        //Check if the result of the query is empty
        Assert.assertNotNull(cursorShouldBeEmpty)
        Assert.assertEquals(0, cursorShouldBeEmpty?.count)
    }

    private fun createProperty1(): Property {
        return Property(123, "house", "100000", "125", "4", "2", "1", "description",
                null, null, null, "12 flower street", null, null, false,
                null, "14/05/2020", "jack", 48.8527288, 2.3505635)
    }
}
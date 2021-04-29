package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider
import com.openclassrooms.realestatemanager.room.RealEstateDatabase
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    private lateinit var database: RealEstateDatabase
    private lateinit var mContentResolver: ContentResolver

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context, RealEstateDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testIfContentProviderQueryWork() {
        val cursor = mContentResolver.query(
                ContentUris.withAppendedId(RealEstateContentProvider.CONTENT_URI,
                        PROPERTY_ID),
                null, null, null, null)
        assertThat(cursor, notNullValue())
        cursor?.getColumnIndexOrThrow("id")
        cursor?.getColumnIndexOrThrow( "property_type")
        cursor?.getColumnIndexOrThrow( "property_price")
        cursor?.getColumnIndexOrThrow( "property_surface")
        cursor?.getColumnIndexOrThrow( "property_room")
        cursor?.getColumnIndexOrThrow( "property_bedroom")
        cursor?.getColumnIndexOrThrow( "property_bathroom")
        cursor?.getColumnIndexOrThrow( "property_description")
        cursor?.getColumnIndexOrThrow( "property_photo")
        cursor?.getColumnIndexOrThrow( "property_description_photo")
        cursor?.getColumnIndexOrThrow( "property_video")
        cursor?.getColumnIndexOrThrow( "property_address")
        cursor?.getColumnIndexOrThrow( "property_asset")
        cursor?.getColumnIndexOrThrow( "property_poi")
        cursor?.getColumnIndexOrThrow( "property_sold")
        cursor?.getColumnIndexOrThrow( "property_sold_date")
        cursor?.getColumnIndexOrThrow( "property_arrival_date")
        cursor?.getColumnIndexOrThrow( "property_agent")
        cursor?.getColumnIndexOrThrow( "property_Lat")
        cursor?.getColumnIndexOrThrow( "property_Long")
        cursor?.close()
    }

    companion object {
        const val PROPERTY_ID: Long = 1
    }
}
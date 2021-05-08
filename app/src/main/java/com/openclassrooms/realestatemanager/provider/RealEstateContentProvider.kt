package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.room.RealEstateDatabase

class RealEstateContentProvider : ContentProvider() {


    override fun onCreate(): Boolean {
        return true
    }

    //query data from database
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor {
        if (context != null) {
            val index = ContentUris.parseId(uri)
            val cursor = RealEstateDatabase.getDatabase(context!!).propertyDao().getPropertiesWithCursor(index.toInt())
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    companion object {
        private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"
        private const val TABLE_NAME = "property_items"
        val URI_PROPERTY: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

}
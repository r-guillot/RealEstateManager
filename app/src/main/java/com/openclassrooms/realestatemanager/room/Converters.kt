package com.openclassrooms.realestatemanager.room

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.Transformations.map
import androidx.room.TypeConverter

private const val SEPARATOR = ","

class Converters {

    //TypeConverter for mutableList with Uri
    @TypeConverter
    fun uriToString(uri: MutableList<Uri>?): String? =
            uri?.map { it }?.joinToString(separator = SEPARATOR)

    @TypeConverter
    fun stringToUri(uri: String?): MutableList<Uri>? =
            uri?.split(SEPARATOR)?.map { it.toUri() }?.toMutableList()

    @TypeConverter
    fun fromString(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri.toString()
    }

    @TypeConverter
    fun stringToString(string: MutableList<String>?): String? =
            string?.joinToString(separator = SEPARATOR) { it }

    @TypeConverter
    fun stringToString(string: String?): MutableList<String>? =
            string?.split(SEPARATOR)?.map { it.toString() }?.toMutableList()
}
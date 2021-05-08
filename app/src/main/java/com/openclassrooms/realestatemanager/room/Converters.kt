package com.openclassrooms.realestatemanager.room

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

private const val SEPARATOR = ","

class Converters {
    companion object {
        //TypeConverter for room database

        //TypeConverter for mutableList with Uri
        @TypeConverter
        @JvmStatic
        fun uriToString(uri: MutableList<Uri>?): String? =
                uri?.map { it }?.joinToString(separator = SEPARATOR)

        @TypeConverter
        @JvmStatic
        fun stringToUri(uri: String?): MutableList<Uri>? =
                uri?.split(SEPARATOR)?.map { it.toUri() }?.toMutableList()

        //TypeConverter for video uri
        @TypeConverter
        @JvmStatic
        fun fromString(value: String?): Uri? {
            return if (value == null) null else Uri.parse(value)
        }

        @TypeConverter
        @JvmStatic
        fun fromUri(uri: Uri?): String {
            return uri.toString()
        }

        //TypeConverter for mutableList with String
        @TypeConverter
        @JvmStatic
        fun stringToString(string: MutableList<String>?): String? =
                string?.joinToString(separator = SEPARATOR) { it }

        @TypeConverter
        @JvmStatic
        fun stringToString(string: String?): MutableList<String>? =
                string?.split(SEPARATOR)?.map { it }?.toMutableList()
    }
}
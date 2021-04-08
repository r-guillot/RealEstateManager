package com.openclassrooms.realestatemanager.room

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64.encodeToString
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.util.*

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


    @TypeConverter
    fun fromBitmap(bmp: MutableList<Bitmap>?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        return bmp?.map {it.compress(Bitmap.CompressFormat.PNG, 100, outputStream) }?.let { outputStream.toByteArray() }
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray?): MutableList<Bitmap> {
        return bytes?.map { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) } as MutableList<Bitmap>
    }
}
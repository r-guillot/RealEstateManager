package com.openclassrooms.realestatemanager.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "property_items")
data class Property(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "property_type") var type: String?,
        @ColumnInfo(name = "property_price") var price: String?,
        @ColumnInfo(name = "property_surface") var surface: String?,
        @ColumnInfo(name = "property_room") var room: String?,
        @ColumnInfo(name = "property_bedroom") var bedroom: String?,
        @ColumnInfo(name = "property_bathroom") var bathroom: String?,
        @ColumnInfo(name = "property_description") var description: String?,
        @ColumnInfo(name = "property_photo") var photo: MutableList<Uri>?,
        @ColumnInfo(name = "property_video") var video: Uri?,
        @ColumnInfo(name = "property_address") var address: String?,
        @ColumnInfo(name = "property_asset") var asset: MutableList<String>?,
        @ColumnInfo(name = "property_poi") var pointOfInterest: MutableList<String>?,
        @ColumnInfo(name = "property_sold") var sold: Boolean,
        @ColumnInfo(name = "property_sold_date") var soldDate: String?,
        @ColumnInfo(name = "property_arrival_date") var arrivalDate: String,
        @ColumnInfo(name = "property_agent") val agent: String,
        @ColumnInfo(name = "property_Lat") var propertyLat: Double,
        @ColumnInfo(name = "property_Long") var propertyLong: Double

)
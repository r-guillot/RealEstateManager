package com.openclassrooms.realestatemanager.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_items")
data class Property(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "property_type") var type: String?,
        @ColumnInfo(name = "property_price") var price: Int?,
        @ColumnInfo(name = "property_surface") var surface: Int?,
        @ColumnInfo(name = "property_room") var room: Int?,
        @ColumnInfo(name = "property_bedroom") var bedroom: Int?,
        @ColumnInfo(name = "property_bathroom") var bathroom: Int?,
        @ColumnInfo(name = "property_description") var description: String?,
        @ColumnInfo(name = "property_photo") var photo: MutableList<Uri?>?,
        @ColumnInfo(name = "property_address") var address: String?,
        @ColumnInfo(name = "property_asset") var asset: MutableList<CharSequence?>?,
        @ColumnInfo(name = "property_poi") var pointOfInterest: MutableList<CharSequence?>?,
        @ColumnInfo(name = "property_sold") var sold: Boolean,
        @ColumnInfo(name = "property_sold_date") var soldDate: String?,
        @ColumnInfo(name = "property_arrival_date") var arrivalDate: String,
        @ColumnInfo(name = "property_agent") val agent: String
)
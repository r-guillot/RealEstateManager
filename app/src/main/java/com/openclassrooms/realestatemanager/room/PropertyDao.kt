package com.openclassrooms.realestatemanager.room

import android.database.Cursor
import android.net.Uri
import androidx.room.*
import com.openclassrooms.realestatemanager.model.Property
import kotlinx.coroutines.flow.Flow


@Dao
interface PropertyDao {
    @Query("SELECT * FROM property_items ORDER BY property_arrival_date DESC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM property_items WHERE id LIKE :id")
    fun findById(id: Int): Property

    @Query("SELECT * FROM property_items WHERE id = :id")
    fun getPropertiesWithCursor(id: Int): Cursor

    @Insert
    suspend fun insertProperty(vararg property: Property)

    @Update
    suspend fun updateProperty(vararg property: Property)
}
package com.openclassrooms.realestatemanager.room

import androidx.room.*
import com.openclassrooms.realestatemanager.model.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property_items ORDER BY property_arrival_date DESC")
    fun getAllProperty(): Flow<List<Property>>

    @Query("SELECT * FROM property_items WHERE property_type LIKE :type")
    fun findByType(type: String): Flow<List<Property>>

    @Query("SELECT * FROM property_items WHERE id LIKE :id")
    fun findById(id: Int): Property

    @Insert
    suspend fun insertProperty(vararg property: Property)

    @Query("DELETE FROM property_items")
    suspend fun deleteAllProperty()

    @Update
    fun updateProperty(vararg property: Property)
}
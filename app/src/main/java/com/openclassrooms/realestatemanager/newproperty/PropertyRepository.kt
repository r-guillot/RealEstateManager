package com.openclassrooms.realestatemanager.newproperty

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.PropertyDao
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyDao: PropertyDao) {

    val allProperties: Flow<List<Property>> = propertyDao.getAllProperty()
//    val propertiesByType: Flow<List<Property>> = propertyDao.findByType()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertProperty(property: Property) {
        propertyDao.insertProperty(property)
    }


    fun getProperty(int: Int): Property {
       return propertyDao.findById(int)
    }
}
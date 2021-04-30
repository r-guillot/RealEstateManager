package com.openclassrooms.realestatemanager.newproperty

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.PropertyDao
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyDao: PropertyDao) {

    val allProperties: Flow<List<Property>> = propertyDao.getAllProperties()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertProperty(property: Property) {
        propertyDao.insertProperty(property)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateProperty(property: Property){
        propertyDao.updateProperty(property)
    }
}
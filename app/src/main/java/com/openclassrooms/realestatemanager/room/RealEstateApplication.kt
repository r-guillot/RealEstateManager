package com.openclassrooms.realestatemanager.room

import android.app.Application
import com.openclassrooms.realestatemanager.newproperty.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val realEstateDatabase by lazy {  RealEstateDatabase.getDatabase(this) }
    val propertyRepository by lazy { PropertyRepository(realEstateDatabase.propertyDao()) }
}
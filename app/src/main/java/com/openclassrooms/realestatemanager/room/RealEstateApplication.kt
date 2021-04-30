package com.openclassrooms.realestatemanager.room

import android.app.Application
import com.openclassrooms.realestatemanager.newproperty.PropertyRepository

class RealEstateApplication : Application() {
    private val realEstateDatabase by lazy {  RealEstateDatabase.getDatabase(this) }
    val propertyRepository by lazy { PropertyRepository(realEstateDatabase.propertyDao()) }
}
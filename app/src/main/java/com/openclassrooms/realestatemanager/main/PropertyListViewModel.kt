package com.openclassrooms.realestatemanager.main

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.PropertyRepository

class PropertyListViewModel(private val propertyRepository: PropertyRepository): ViewModel() {

    val allProperty: LiveData<List<Property>> = propertyRepository.allProperties.asLiveData()

    fun getProperty(int: Int): Property {
        return propertyRepository.getProperty(int)
    }
}

class PropertyListViewModelFactory(private val propertyRepository: PropertyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyListViewModel(propertyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

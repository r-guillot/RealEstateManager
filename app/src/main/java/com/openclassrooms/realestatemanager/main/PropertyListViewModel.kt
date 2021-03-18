package com.openclassrooms.realestatemanager.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.PropertyRepository

class PropertyListViewModel(private val propertyRepository: PropertyRepository): ViewModel() {

    val allProperty: LiveData<List<Property>> = propertyRepository.allProperties.asLiveData()
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

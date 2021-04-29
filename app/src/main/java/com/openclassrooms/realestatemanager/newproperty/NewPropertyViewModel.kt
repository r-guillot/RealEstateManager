package com.openclassrooms.realestatemanager.newproperty

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.model.Property
import kotlinx.coroutines.launch

class NewPropertyViewModel(private val propertyRepository: PropertyRepository): ViewModel() {

    val allProperty: LiveData<List<Property>> = propertyRepository.allProperties.asLiveData()

    fun insertNewProperty(property: Property)=viewModelScope.launch {
        propertyRepository.insertProperty(property)
    }

    fun updateProperty(property: Property)= viewModelScope.launch {
        propertyRepository.updateProperty(property)
    }

    fun getCord(address: String, context: Context): MutableList<Address>? {
        val cord = Geocoder(context)
        return cord.getFromLocationName(address, 1)
    }
}

class NewPropertyViewModelFactory(private val propertyRepository: PropertyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewPropertyViewModel(propertyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
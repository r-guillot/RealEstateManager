package com.openclassrooms.realestatemanager.utils

interface FragmentCallback {
    //fragment callback used for communication between FilterFragment and PropertyListActivity
    fun onPropertyFiltered(mutableList: MutableList<com.openclassrooms.realestatemanager.model.Property>)
}
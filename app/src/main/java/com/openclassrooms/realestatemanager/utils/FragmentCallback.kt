package com.openclassrooms.realestatemanager.utils

interface FragmentCallback {
    fun onPropertyFiltered(mutableList: MutableList<com.openclassrooms.realestatemanager.model.Property>)
}
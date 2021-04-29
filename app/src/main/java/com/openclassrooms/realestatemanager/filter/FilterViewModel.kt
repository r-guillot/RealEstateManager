package com.openclassrooms.realestatemanager.filter

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.model.Property

class FilterViewModel : ViewModel() {
    fun filterRoom(filterList: List<Property>, room: String): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.room.isNullOrEmpty()) {
                if (property.room?.toInt()!! >= room.toInt()) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

    fun filterBedroom(filterList: List<Property>, bedroom: String): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.bedroom.isNullOrEmpty()) {
                if (property.bedroom?.toInt()!! >= bedroom.toInt()) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

    fun filterPrice(filterList: List<Property>, priceMin: String, priceMax: String): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.price.isNullOrEmpty()) {
                if (property.price?.toInt()!! in priceMin.toInt()..priceMax.toInt()) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

    fun filterSurface(filterList: List<Property>, surfaceMin: String, surfaceMax: String): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.surface.isNullOrEmpty()) {
                if (property.surface?.toInt()!! in surfaceMin.toInt()..surfaceMax.toInt()) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

    fun filterType(filterList: List<Property>, type: CharSequence?): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.type.isNullOrEmpty()) {
                if (type.toString() == property.type) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

    fun filterAsset(filterList: List<Property>, assetList: List<String>): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.asset.isNullOrEmpty()) {
                if (property.asset!!.containsAll(assetList)) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

    fun filterInterest(filterList: List<Property>, interestList: List<String>): MutableList<Property> {
        val viewModelList: MutableList<Property> = mutableListOf()
        for (property in filterList) {
            if (!property.pointOfInterest.isNullOrEmpty()) {
                if (property.pointOfInterest!!.containsAll(interestList)) {
                    viewModelList.add(property)
                }
            }
        }
        return viewModelList
    }

}
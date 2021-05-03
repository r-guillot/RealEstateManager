package com.openclassrooms.realestatemanager.utils

import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.openclassrooms.realestatemanager.model.Property

object ViewHelper {
    fun checkOnlySelectedChips(chipGroup: ChipGroup, list: MutableList<String>?) {
        for (id in chipGroup.checkedChipIds) {
            val chip: Chip = chipGroup.findViewById(id)
            if (!list!!.contains(chip.text.toString())) {
                chip.isChecked = false
            }
        }
    }

    fun checkSelectedType(property: Property, chip: Chip) {
        if (property.type?.contains(chip.text.toString()) == true) {
            chip.isChecked = true
                if (!property.type!!.contains(chip.text.toString())) {
                    chip.isChecked = false
                }
            }
        }
}
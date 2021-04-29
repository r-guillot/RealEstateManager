package com.openclassrooms.realestatemanager.utils

fun String.checkIfEmpty(unit: String): String {
    return if (this.isEmpty()) {
        "no info"
    } else {
        this + unit
    }
}
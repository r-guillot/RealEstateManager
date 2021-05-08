package com.openclassrooms.realestatemanager.utils

fun Int.twoNumberFormat(): String {
    return if (this <= 9) "0$this" else this.toString()
}
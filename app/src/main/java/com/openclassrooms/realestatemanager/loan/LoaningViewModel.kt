package com.openclassrooms.realestatemanager.loan

import androidx.lifecycle.ViewModel
import kotlin.math.pow
import kotlin.math.roundToInt

class LoaningViewModel: ViewModel() {
    private var totalLoaningTResult: Int = 0
    private var months: Int = 0

    //calculation for a loaning
    fun calculateTotalLoaning(secureRate: Double, years: Int, secureAmount: Double, secureDown: Double): Int{
        val interest = secureRate / 100 / 12
        months = years.times(12)
        totalLoaningTResult = if (secureAmount == 0.0 || secureRate == 0.0 || months == 0) 0
        else
            (((secureAmount - secureDown) * (interest * (1 + interest).pow(months) / ((1 + interest).pow(months) - 1))) * months).roundToInt()
        return totalLoaningTResult
    }

    fun calculateMonthlyLoaning(): Int{
       return totalLoaningTResult/months
    }
}
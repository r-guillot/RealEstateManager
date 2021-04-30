package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.filter.FilterViewModel
import com.openclassrooms.realestatemanager.loan.LoaningViewModel
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    private var amount = 10
    private lateinit var loaningViewModel: LoaningViewModel
    private lateinit var filterViewModel: FilterViewModel
    private val years = 20
    private val loanAmount = 300000.0
    private val rate = 1.34
    private val expectedTotalResult = 342160
    private val expectedMonthlyResult = 1425
    private lateinit var propertiesList: MutableList<Property>

    @Before
    fun setUp(){
        loaningViewModel = LoaningViewModel()
        filterViewModel = FilterViewModel()
        propertiesList = mutableListOf(Property(0, "house", "100000", "125", "4", "2", "1", "description",
               null, null, null, "12 flower street",null , null , false,
                null, "14/05/2020", "jack", 48.8527288, 2.3505635),
        Property(1, "house", "150000", "250", "6", "3", "2", "description",
                null, null, null, null,null , null , false,
                null, "18/05/2020", "john", 48.8527288, 2.3505635),
        Property(2, "condo", "75000", "75", "2", "1", "1", "description",
                null, null, null, "135 downtown boulevard",null , null , false,
                null, "25/07/2020", "jean", 48.8527288, 2.3505635))
    }

    @Test
    fun checkIfConverterDollarToEuroWork() {
        val expectedResult = 8.12.toInt()
        val result = Utils.convertDollarToEuro(amount)
        Assert.assertEquals(expectedResult.toLong(), result.toLong())
    }

    @Test
    fun checkIfConverterEuroToDollarWork() {
        val expectedResult = 12.32.toInt()
        val result = Utils.convertEuroToDollar(amount)
        Assert.assertEquals(expectedResult.toLong(), result.toLong())
    }

    @Test
    fun checkIfTheDateIsDisplayProperly() {
        val calendar = Calendar.getInstance()
        val dateOfToday = Utils.getTodayDate()
        val dateFormatForDateTested: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateTested = dateFormatForDateTested.format(calendar.time)
        val dateOfTodayFormat: DateFormat = SimpleDateFormat(dateOfToday)
        val dateFormatExpected: DateFormat = SimpleDateFormat(dateTested)
        Assert.assertEquals(dateFormatExpected, dateOfTodayFormat)
        println(dateOfToday)
    }

    @Test
    fun checkIfLoaningCalculateCorrectly(){
        Assert.assertFalse(loaningViewModel.calculateTotalLoaning(0.0,years,loanAmount,0.0) != 0)
        Assert.assertFalse(loaningViewModel.calculateTotalLoaning(rate,0,loanAmount,0.0) != 0)
        Assert.assertFalse(loaningViewModel.calculateTotalLoaning(rate,years,0.0,0.0) != 0)
        Assert.assertTrue(loaningViewModel.calculateTotalLoaning(rate,years,loanAmount,0.0) != 0)
        Assert.assertTrue(loaningViewModel.calculateTotalLoaning(rate,years,loanAmount,0.0) == expectedTotalResult)
        Assert.assertTrue(loaningViewModel.calculateMonthlyLoaning() == expectedMonthlyResult)
    }

    @Test
    fun checkIfPropertiesCanBeFiltered(){
        Assert.assertTrue(propertiesList.size == 3)
        Assert.assertEquals(1, filterViewModel.filterType(propertiesList, "condo").size)
        Assert.assertEquals(2, filterViewModel.filterBedroom(propertiesList, "2").size)
        Assert.assertEquals(3, filterViewModel.filterPrice(propertiesList, "75000","150000").size)
        Assert.assertEquals(1,filterViewModel.filterBedroom(filterViewModel.filterPrice(propertiesList, "75000","150000"),"3").size)
    }
}
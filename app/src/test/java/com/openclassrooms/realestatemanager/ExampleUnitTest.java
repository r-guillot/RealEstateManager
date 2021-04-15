package com.openclassrooms.realestatemanager;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    int amount = 10;

    @Test
    public void checkIfConverterDollarToEuroWork() {
        int expectedResult = (int) 8.12;
        int result =Utils.convertDollarToEuro(amount);
        assertEquals(expectedResult,result);
    }

    @Test
    public void checkIfConverterEuroToDollarWork() {
        int expectedResult = (int) 12.32;
        int result =Utils.convertEuroToDollar(amount);
        assertEquals(expectedResult,result);
    }

    @Test
    public void checkIfTheDateIsDisplayProperly() {
        Calendar calendar = Calendar.getInstance();
        String dateOfToday = Utils.getTodayDate();

        DateFormat dateFormatForDateTested= new SimpleDateFormat("dd/MM/yyyy");
        String dateTested = dateFormatForDateTested.format(calendar.getTime());

        DateFormat dateOfTodayFormat = new SimpleDateFormat(dateOfToday);
        DateFormat dateFormatExpected = new SimpleDateFormat(dateTested);

        assertEquals(dateFormatExpected,dateOfTodayFormat);
        System.out.println(dateOfToday);
    }
}
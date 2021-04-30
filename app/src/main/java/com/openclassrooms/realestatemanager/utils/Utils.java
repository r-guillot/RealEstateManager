package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euro) {
        return (int) Math.round(euro / 0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     */
//    public static String getTodayDate(){
//        DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
//        return dateFormat.format(new Date());
//    }

    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     */
//    public static Boolean isInternetAvailable(Context context){
//        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//        return wifi.isWifiEnabled();
//    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static boolean hasGPSOn(Context context){
        boolean gpsInfo = false;
        LocationManager locationManager =
                (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        try {
            gpsInfo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gpsInfo;
    }
}

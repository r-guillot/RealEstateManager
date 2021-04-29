package com.openclassrooms.realestatemanager.map

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.detail.PropertyDetailActivity
import com.openclassrooms.realestatemanager.detail.PropertyDetailFragment
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.RealEstateApplication

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //Use map when it ready, and populate it with marker
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        checkForPermission()
        viewModel.allProperty.observe(this,this::initRestaurantMarker)
    }

    //After checking the internet connection of user
    // Check for user location permission, ask him if we don't have it
    //Move the map to user's location
    private fun checkForPermission() {
        if (Utils.hasInternetConnection(this)) {
            if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                return
            }
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        } else {
            Toast.makeText(this,getString(R.string.noConnection),Toast.LENGTH_SHORT).show()
        }
    }

    //Adding a marker to the map for each existing property
    private fun initRestaurantMarker(properties: List<Property>) {
        mMap.clear()
        for (property in properties) {
            val propertyLocation = LatLng(property.propertyLat, property.propertyLong)
            val marker = mMap.addMarker(MarkerOptions().position(propertyLocation))
            marker.tag = property.id
        }
        mMap.setOnMarkerClickListener(this)
    }

    //Go to PropertyDetailActivity when a marker is clicked
    override fun onMarkerClick(p0: Marker?): Boolean {
        val propertyId = p0?.tag as Int

        val intent = Intent(this, PropertyDetailActivity::class.java).apply {
            putExtra(PropertyDetailFragment.ARG_ITEM_ID, propertyId.toString())
        }
        startActivity(intent)
        return false
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

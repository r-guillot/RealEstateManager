package com.openclassrooms.realestatemanager.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityCoreBinding
import com.openclassrooms.realestatemanager.room.RealEstateApplication


class CoreActivity : AppCompatActivity(), NavController.OnDestinationChangedListener{
    private lateinit var binding: ActivityCoreBinding
    private lateinit var navController: NavController

    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        configureBinding()
        initNavigation()
        getProperties()
        navController.addOnDestinationChangedListener(this)
    }

    private fun configureBinding() {
        binding = ActivityCoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun initNavigation(){
        val navHostFragment =
                supportFragmentManager.findFragmentById(binding.coreFragmentContainer.id) as NavHostFragment
//        navController = navHostFragment.findNavController()
        navController = navHostFragment.navController
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // action when user clicks home button
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when (destination.id) {
            R.id.detailFragment -> {
                supportActionBar?.setHomeButtonEnabled(!resources.getBoolean(R.bool.isLandscape))
                supportActionBar?.setDisplayHomeAsUpEnabled(!resources.getBoolean(R.bool.isLandscape))
            }
            else -> {
                supportActionBar?.setHomeButtonEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun getProperties() {
        viewModel.allProperty.observe(this, Observer { properties ->
            properties.let { Toast.makeText(this,"ok", Toast.LENGTH_SHORT).show() }
        })
    }
}
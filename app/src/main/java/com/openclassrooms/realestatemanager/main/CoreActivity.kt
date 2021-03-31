//package com.openclassrooms.realestatemanager.main
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.lifecycle.Observer
//import androidx.navigation.NavController
//import androidx.navigation.NavDestination
//import androidx.navigation.fragment.NavHostFragment
//import com.openclassrooms.realestatemanager.R
//import com.openclassrooms.realestatemanager.databinding.ActivityCoreBinding
//import com.openclassrooms.realestatemanager.room.RealEstateApplication
//
//
//class CoreActivity : AppCompatActivity(), NavController.OnDestinationChangedListener{
//    private lateinit var binding: ActivityCoreBinding
//    private lateinit var navController: NavController
//
//    private lateinit var navHostFragment: NavHostFragment
//
//    private val viewModel: PropertyListViewModel by viewModels {
//        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_core)
//
//        configureBinding()
//        initNavigation()
//    }
//
//    private fun configureBinding() {
//        binding = ActivityCoreBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//    }
//
//    private fun initNavigation(){
//        navHostFragment = supportFragmentManager.findFragmentById(R.id.core_fragment_container) as NavHostFragment
//        navHostFragment.navController.addOnDestinationChangedListener(this)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            // action when user clicks home button
////            android.R.id.home -> onBackPressed()
//            android.R.id.home -> {
//                navHostFragment.navController.popBackStack()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
//        when (destination.id) {
//            R.id.detailFragment -> {
//                supportActionBar?.setHomeButtonEnabled(!resources.getBoolean(R.bool.isTablet))
//                supportActionBar?.setDisplayHomeAsUpEnabled(!resources.getBoolean(R.bool.isTablet))
//            }
//            else -> {
//                supportActionBar?.setHomeButtonEnabled(false)
//                supportActionBar?.setDisplayHomeAsUpEnabled(false)
//            }
//        }
//    }
//}
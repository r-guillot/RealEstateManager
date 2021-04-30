package com.openclassrooms.realestatemanager.propertylist

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.utils.FragmentCallback
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.databinding.PropertyListBinding
import com.openclassrooms.realestatemanager.detail.PropertyDetailActivity
import com.openclassrooms.realestatemanager.detail.PropertyDetailFragment
import com.openclassrooms.realestatemanager.filter.FilterFragment
import com.openclassrooms.realestatemanager.loan.LoaningActivity
import com.openclassrooms.realestatemanager.map.MapsActivity
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.NewPropertyActivity
import com.openclassrooms.realestatemanager.room.RealEstateApplication

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [PropertyDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class PropertyListActivity : AppCompatActivity(), FragmentCallback {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
    }
    private lateinit var binding: PropertyListBinding

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureBinding()
        getProperties()
        checkForPermission()
        goToChosenActivity()
        filterFabClick()

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
    }

    private fun configureBinding() {
        binding = PropertyListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getProperties() {
        viewModel.allProperty.observe(this, { properties ->
            configureRecycleView(properties)
        })
    }

    private fun configureRecycleView(propertyList: List<Property>) {
        binding.propertyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PropertyListActivity)
            adapter = PropertyListAdapter(propertyList) { property -> itemClicked(property) }
            if (twoPane && propertyList.isNotEmpty()){itemClicked(propertyList[0])}
        }
    }

    //check for permission for location of user
    private fun checkForPermission() {
        if (Utils.hasInternetConnection(this)) {
            if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                return
            }
        }
    }

    //go to PropertyDetailActivity when an item is clicked
    private fun itemClicked(property: Property) {
        if (twoPane) {
            val fragment = PropertyDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PropertyDetailFragment.ARG_ITEM_ID, property.id.toString())
                }
            }
            this.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
        } else {
            val intent = Intent(this, PropertyDetailActivity::class.java).apply {
                putExtra(PropertyDetailFragment.ARG_ITEM_ID, property.id.toString())
            }
            startActivity(intent)
        }
    }

    private fun filterFabClick(){
        binding.FABFilter.setOnClickListener {
            FilterFragment().apply {
                show(supportFragmentManager, FilterFragment.TAG)
            }
        }
    }

    override fun onPropertyFiltered(mutableList: MutableList<Property>) {
        configureRecycleView(mutableList)
    }

    //select activity you want after clicked on speedDial button
    private fun goToChosenActivity() {
        binding.speedDial.inflate(R.menu.fab_speed_dial_menu)
        binding.speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.action_new_property -> {
                    intent = Intent(this@PropertyListActivity, NewPropertyActivity::class.java)
                }
                R.id.action_map -> {
                    //check if user have connection and can be geolocation
                    if (Utils.hasInternetConnection(this) && Utils.hasGPSOn(this)) {
                        intent = Intent(this@PropertyListActivity, MapsActivity::class.java)
                    } else {
                        val errorMessage = getString(R.string.noConnection) + getString(R.string.or) + getString(R.string.noGPS)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
                R.id.action_loan -> {
                    intent = Intent(this@PropertyListActivity, LoaningActivity::class.java)
                }
            }
            intent?.let { startActivity(it) }
            false
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
package com.openclassrooms.realestatemanager.propertylist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PropertyListBinding
import com.openclassrooms.realestatemanager.detail.PropertyDetailActivity
import com.openclassrooms.realestatemanager.detail.PropertyDetailFragment
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
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
class PropertyListActivity : AppCompatActivity() {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
    }
    private lateinit var binding: PropertyListBinding
    private lateinit var adapter: PropertyListAdapter

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_list)

        configureBinding()
        getProperties()
        goToNewPropertyActivity()

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
            Log.d("TAG", "twoPane: $twoPane")
        }
    }

    private fun configureBinding() {
        binding = PropertyListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getProperties() {
        viewModel.allProperty.observe(this, Observer { properties ->
            configureRecycleView(properties)
            Log.d("RV", "getProperties: $properties")
        })
    }

    private fun configureRecycleView(propertyList: List<Property>) {
        binding.propertyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PropertyListActivity)
            adapter = PropertyListAdapter(propertyList, twoPane) { property -> itemClicked(property) }
        }
    }

    private fun itemClicked(property: Property) {
        Toast.makeText(this, "Clicked: ${property.id}", Toast.LENGTH_SHORT).show()
        if (twoPane) {
            val fragment = PropertyDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PropertyDetailFragment.ARG_ITEM_ID, property.id.toString())
                    Log.d("TAG", "propertyId ${property.id} + argument $arguments")

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

    private fun goToNewPropertyActivity() {
        binding.fabNewProperty.setOnClickListener {
            startActivity(Intent(this, NewPropertyActivity::class.java))
        }
    }
}
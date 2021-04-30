package com.openclassrooms.realestatemanager.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.openclassrooms.realestatemanager.propertylist.PropertyListActivity
import com.openclassrooms.realestatemanager.R

/**
 * An activity representing a single Property detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [PropertyListActivity].
 */
class PropertyDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don"t need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = PropertyDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PropertyDetailFragment.ARG_ITEM_ID,
                            intent.getStringExtra(PropertyDetailFragment.ARG_ITEM_ID))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit()
        }
    }
}
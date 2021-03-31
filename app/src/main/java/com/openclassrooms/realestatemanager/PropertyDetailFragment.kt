package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.RealEstateApplication

/**
 * A fragment representing a single Property detail screen.
 * This fragment is either contained in a [PropertyListActivity]
 * in two-pane mode (on tablets) or a [PropertyDetailActivity]
 * on handsets.
 */
class PropertyDetailFragment : Fragment() {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }

    /**
     * The dummy content this fragment is presenting.
     */
    private var property: Property? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                property = (it.getString(ARG_ITEM_ID))?.let { it1 -> viewModel.getProperty(it1.toInt()) }
                Log.d("TAG", "onCreate: $property")
//                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = property?.content
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.property_detail, container, false)

        // Show the dummy content as text in a TextView.
        property?.let {
            rootView.findViewById<TextView>(R.id.testTextView).text = it.type
            Log.d("TAG", "onCreateView: $property")
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
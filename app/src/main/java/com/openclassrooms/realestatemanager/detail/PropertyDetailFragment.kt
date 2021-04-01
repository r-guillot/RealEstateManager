package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.databinding.PropertyDetailBinding
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.ViewPagerAdapter
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

    private lateinit var binding: PropertyDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PropertyDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun getProperties(propertyList: List<Property>) {
        val property = propertyList.find { it.id == arguments?.getString(ARG_ITEM_ID)?.toInt() }
        Log.d("TAG", "onCreate: $property + argument $arguments")
        property?.let { updateUi(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                viewModel.allProperty.observe(viewLifecycleOwner, Observer { properties ->
                    getProperties(properties)
                    Log.d("TAG", "getProperties: $properties + argument $arguments")
                })
            }
        }
    }

    private fun updateUi(property: Property) {
        Log.d("TAG", "onViewCreated: $property")
        binding.apply {
            textViewPropertyType.text = property.type
            textViewPropertyPlace.text = property.address
            textViewPropertySurface.text = property.surface.toString()
            textViewNumberRooms.text = property.room.toString()
            textViewNumberBedroom.text = property.bedroom.toString()
            textViewNumberBathroom.text = property.bathroom.toString()
            textViewPropertyPrice.text = property.price.toString()
            textViewDescription.text = property.description
            textViewPropertyOnlineDate.text = property.arrivalDate
            property.photo?.let { displaySelectedPhotoInViewPager(property) }
        }
    }

    private fun displaySelectedPhotoInViewPager(property: Property) {
        if (property.photo?.size!! > 0) {
            val adapter = activity?.let { ViewPagerAdapter(property.photo!!, it.baseContext, 1.0f) }
            binding.viewPager.adapter = adapter
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
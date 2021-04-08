package com.openclassrooms.realestatemanager.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.OnMapAndViewReadyListener
import com.openclassrooms.realestatemanager.R
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
class PropertyDetailFragment : Fragment(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }

    private lateinit var binding: PropertyDetailBinding
    private lateinit var map: GoogleMap
    private lateinit var videoUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PropertyDetailBinding.inflate(layoutInflater)

        binding.viewPager.requestFocus()
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
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
            textViewPropertyType.text = checkIfNull(property.type.toString(), "")
            textViewPropertyPlace.text = checkIfNull(property.address.toString(), "")
            textViewPropertySurface.text = checkIfNull(property.surface.toString(), " m²")
            textViewNumberRooms.text = checkIfNull(property.room.toString(), " rooms")
            textViewNumberBedroom.text = checkIfNull(property.bedroom.toString(), " bedrooms")
            textViewNumberBathroom.text = checkIfNull(property.bathroom.toString(), " bathrooms")
            textViewPropertyPrice.text = checkIfNull(property.price.toString(), " $")
            textViewDescription.text = checkIfNull(property.description.toString(), "")
            textViewPropertyOnlineDate.text = property.arrivalDate
            property.photo?.let { displaySelectedPhotoInViewPager(property) }
            property.asset?.let { addAsset(it) }
            property.pointOfInterest?.let { addInterest(it) }

            showPropertyPosition(property)
            Log.d("FS", "updateUi: ${property.video}")

            if (property.video != null) {
                videoUri = property.video!!
            }
            displayVideo(videoUri)
        }
    }

    private fun displaySelectedPhotoInViewPager(property: Property) {
        if (property.photo?.size!! > 0) {
            val adapter = activity?.let { ViewPagerAdapter(property.photo!!, it.baseContext, 1.0f) }
            binding.viewPager.adapter = adapter
        }
    }

    private fun checkIfNull(info: String, unit: String): String {
        return if (info.contains("null")) {
            "no info"
        } else {
            info + unit
        }
    }

    private fun addAsset(assetList: MutableList<String>) {
        val chipGroup = binding.chipGroupAsset
        for (i in 0 until assetList.size) {
            val chip = Chip(chipGroup.context)
            chip.text = assetList[i]
            chipGroup.addView(chip)
        }
    }

    private fun addInterest(interestList: MutableList<String>) {
        val chipGroup = binding.chipGroupInterest
        for (i in 0 until interestList.size) {
            val chip = Chip(chipGroup.context)
            chip.text = interestList[i]
            chipGroup.addView(chip)
        }
    }

    private fun showPropertyPosition(property: Property) {
        if (!::map.isInitialized) return

        val propertyPosition = LatLng(property.propertyLat, property.propertyLong)
        // Center camera on Adelaide marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(propertyPosition, 15f))
        map.addMarker(
                MarkerOptions()
                        .position(propertyPosition)
                        .title(property.address)
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // return early if the map was not initialised properly
        map = googleMap ?: return
    }

    private fun displayVideo(videoUri: Uri) {
        val videoView = binding.videoView
        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        videoView.visibility = View.VISIBLE
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.seekTo(1)
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}

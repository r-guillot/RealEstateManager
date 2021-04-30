package com.openclassrooms.realestatemanager.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PropertyDetailBinding
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.NewPropertyActivity
import com.openclassrooms.realestatemanager.newproperty.ViewPagerAdapter
import com.openclassrooms.realestatemanager.propertylist.PropertyListViewModel
import com.openclassrooms.realestatemanager.propertylist.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.room.RealEstateApplication
import com.openclassrooms.realestatemanager.utils.OnMapAndViewReadyListener
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.checkIfEmpty


/**
 * A fragment representing a single Property detail screen.
 * This fragment is either contained in a PropertyListActivity
 * in two-pane mode (on tablets) or a [PropertyDetailActivity]
 * on handsets.
 */
class PropertyDetailFragment : Fragment(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }
    private lateinit var binding: PropertyDetailBinding
    private var propertyMap: Property? = null
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PropertyDetailBinding.inflate(layoutInflater)
        binding.viewPager.requestFocus()
        mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)

        return binding.root
    }

    //get all properties and check if contains the chosen property
    private fun getProperties(propertyList: List<Property>) {
        val property = propertyList.find { it.id == arguments?.getString(ARG_ITEM_ID)?.toInt() }
        property?.let { updateUi(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                viewModel.allProperty.observe(viewLifecycleOwner, { properties ->
                    getProperties(properties)
                })
            }
        }
        editProperty()
    }

    //Update UI with property info
    private fun updateUi(property: Property) {
        this.propertyMap = property
        binding.apply {
            textViewPropertyType.text = property.type.toString().checkIfEmpty("")
            textViewPropertyPlace.text = property.address.toString().checkIfEmpty("")
            textViewPropertySurface.text = property.surface.toString().checkIfEmpty(" m²")
            textViewNumberRooms.text = property.room.toString().checkIfEmpty(" rooms")
            textViewNumberBedroom.text = property.bedroom.toString().checkIfEmpty(" bedrooms")
            textViewNumberBathroom.text = property.bathroom.toString().checkIfEmpty(" bathrooms")
            textViewPropertyPrice.text = property.price.toString().checkIfEmpty(" $")
            textViewDescription.text = property.description.toString().checkIfEmpty("")
            textViewPropertyOnlineDate.text = property.arrivalDate
            textViewAgent.text = property.agent
            property.photo?.let { displaySelectedPhotoInViewPager(property) }
            property.asset?.let { addAsset(it) }
            property.pointOfInterest?.let { addInterest(it) }

            if (!property.video.toString().contains("null")) {
                property.video?.let { displayVideo(it) }
            } else {
                binding.videoView.visibility = View.GONE
            }

            // add filter on viewPager if the property is sold
            if (property.sold) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    binding.viewPager.foreground = ResourcesCompat.getDrawable(binding.root.resources, R.drawable.ic_sold, null)
                }
                textViewPropertySoldDate.visibility = View.VISIBLE
                textViewPropertySoldDate.text = property.soldDate
            }
        }
        showPropertyPosition(property)
    }

    private fun displaySelectedPhotoInViewPager(property: Property) {
        if (!property.photo.isNullOrEmpty()) {
            val adapter = ViewPagerAdapter(property.photo!!, requireContext(), 1.0f, property.descriptionPhoto)
            binding.viewPager.adapter = adapter
        }
    }

    //Add asset and point of interest in chipGroup
    private fun addAsset(assetList: MutableList<String>) {
        val chipGroup = binding.chipGroupAsset
        chipGroup.removeAllViews()
        if (assetList[0].isNotEmpty()) {
            for (i in 0 until assetList.size) {
                val chip = Chip(chipGroup.context)
                chip.text = assetList[i]
                chipGroup.addView(chip)
            }
        } else {
            chipGroup.visibility = View.GONE
            binding.textViewAssetProperty.visibility = View.GONE
        }
    }

    private fun addInterest(interestList: MutableList<String>) {
        val chipGroup = binding.chipGroupInterest
        chipGroup.removeAllViews()
        if (interestList[0].isNotEmpty()) {
            for (i in 0 until interestList.size) {
                val chip = Chip(chipGroup.context)
                chip.text = interestList[i]
                chipGroup.addView(chip)
            }
        } else {
            chipGroup.visibility = View.GONE
            binding.textViewInterest.visibility = View.GONE
        }
    }

    //Show property position if the user has an internet connection
    private fun showPropertyPosition(property: Property) {
        if (!::map.isInitialized) return

        if (propertyMap != null && Utils.hasInternetConnection(context)) {
            val propertyPosition = LatLng(property.propertyLat, property.propertyLong)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(propertyPosition, 15f))
            map.addMarker(
                    MarkerOptions()
                            .position(propertyPosition)
                            .title(property.address)
            )
        } else {
            mapFragment.view?.visibility = View.GONE
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // return early if the map was not initialised properly
        map = googleMap ?: return
        propertyMap?.let { showPropertyPosition(it) }
    }

    //display video if agent add one when he create the property
    //hide the view if no video exist
    private fun displayVideo(videoUri: Uri) {
        val videoView = binding.videoView
        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        binding.root.viewTreeObserver.addOnScrollChangedListener { mediaController.hide() }
        videoView.visibility = View.VISIBLE
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.seekTo(1)
    }

    //mini floating action button for edited the property
    private fun editProperty() {
        binding.FABEdit.setOnClickListener {
            val intent = Intent(activity, NewPropertyActivity::class.java).apply {
                putExtra(NewPropertyActivity.ARG_ITEM_ID, propertyMap?.id.toString())
            }
            startActivity(intent)
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

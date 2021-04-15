package com.openclassrooms.realestatemanager.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import com.openclassrooms.realestatemanager.newproperty.NewPropertyActivity
import com.openclassrooms.realestatemanager.newproperty.ViewPagerAdapter
import com.openclassrooms.realestatemanager.room.RealEstateApplication


/**
 * A fragment representing a single Property detail screen.
 * This fragment is either contained in a [PropertyListActivity]
 * in two-pane mode (on tablets) or a [PropertyDetailActivity]
 * on handsets.
 */
class PropertyDetailFragment : Fragment(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    private val TAG = PropertyDetailFragment::class.qualifiedName
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }

    private lateinit var binding: PropertyDetailBinding

        private var propertyMap: Property? = null
    private lateinit var map: GoogleMap
    private var videoUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PropertyDetailBinding.inflate(layoutInflater)

        binding.viewPager.requestFocus()
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)

        return binding.root
    }

    private fun getProperties(propertyList: List<Property>) {
        val property = propertyList.find { it.id == arguments?.getString(ARG_ITEM_ID)?.toInt() }
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
                })
            }
        }
        editProperty()
    }

    private fun updateUi(property: Property) {
        Log.d(TAG, "onViewCreated: $property")
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
            property.photo?.let { displaySelectedPhotoInViewPager(property) }
            property.asset?.let { addAsset(it) }
            property.pointOfInterest?.let { addInterest(it) }
            Log.d(TAG, "updateUi: ${property.video}")

            if (!property.video.toString().contains("null")) {
                property.video?.let { displayVideo(it) }
            } else {
                binding.videoView.visibility = View.GONE
            }

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
            val adapter = ViewPagerAdapter(property.photo!!, requireContext(), 1.0f)
            binding.viewPager.adapter = adapter
        }
    }

    private fun String.checkIfEmpty(unit: String): String {
        return if (this.isEmpty()) {
            "no info"
        } else {
            this + unit
        }
    }

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

    private fun showPropertyPosition(property: Property) {
        Log.d(TAG, "showPropertyPosition: ${!::map.isInitialized}")
        if (!::map.isInitialized) return

        if ( propertyMap != null) {
            val propertyPosition = LatLng(property.propertyLat, property.propertyLong)
            Log.d(TAG, "showPropertyPosition: $propertyPosition")
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(propertyPosition, 15f))
            map.addMarker(
                    MarkerOptions()
                            .position(propertyPosition)
                            .title(property.address)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // return early if the map was not initialised properly
        Log.d(TAG, "onMapReady: ")
        map = googleMap ?: return
        propertyMap?.let { showPropertyPosition(it) }
    }

    private fun displayVideo(videoUri: Uri) {
        val videoView = binding.videoView
        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        binding.root.viewTreeObserver.addOnScrollChangedListener(OnScrollChangedListener { mediaController.hide() })
        videoView.visibility = View.VISIBLE
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.seekTo(1)
    }

    private fun editProperty(){
        binding.FABEdit.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, NewPropertyActivity::class.java).apply {
                putExtra(NewPropertyActivity.ARG_ITEM_ID, propertyMap?.id.toString())
            }
            startActivity(intent)
        })
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}

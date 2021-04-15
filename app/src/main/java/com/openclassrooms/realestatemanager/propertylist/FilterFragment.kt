package com.openclassrooms.realestatemanager.propertylist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityNewPropertyBinding
import com.openclassrooms.realestatemanager.databinding.FragmentFilterBinding
import com.openclassrooms.realestatemanager.databinding.PropertyDetailBinding
import com.openclassrooms.realestatemanager.detail.PropertyDetailFragment
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.NewPropertyActivity
import com.openclassrooms.realestatemanager.room.RealEstateApplication

class FilterFragment : Fragment() {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }
    private lateinit var binding: FragmentFilterBinding
    private var typeChoice: CharSequence? = null
    private var assetList = mutableListOf<String>()
    private var interestList = mutableListOf<String>()
    private lateinit var surfaceMin: String
    private lateinit var surfaceMax: String
    private lateinit var room: String
    private lateinit var bedroom: String
    private lateinit var priceMin: String
    private lateinit var priceMax: String
    private lateinit var filterList: MutableList<Property>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(layoutInflater)

        getChipType()
        onFilterButtonClick()
        return binding.root
    }

    //getChipType(), getChipAsset() et getChipInterest() get the type, the interests and the assets
    // which selected by the agent for the creation of the property
    private fun getChipType() {
        binding.chipGroupType.setOnCheckedChangeListener { chipGroup, checkedId ->
            typeChoice = chipGroup.findViewById<Chip>(checkedId)?.text
        }
    }

    private fun getChipAsset() {
        for (id in binding.chipGroupAsset.checkedChipIds) {
            val chip: Chip = binding.chipGroupAsset.findViewById(id)
            assetList.add(chip.text.toString())
        }
    }

    private fun getChipInterest() {
        for (id in binding.chipGroupInterest.checkedChipIds) {
            val chip: Chip = binding.chipGroupInterest.findViewById(id)
            interestList.add(chip.text.toString())
        }
    }

    private fun onFilterButtonClick() {
        binding.buttonValidate.setOnClickListener(View.OnClickListener {
            getFilterInfo()
        })
    }

    private fun getFilterInfo() {
        getChipAsset()
        getChipInterest()
        binding.apply {
            surfaceMin = editTextSurfaceMin.text.toString()
            surfaceMax = editTextSurfaceMax.text.toString()
            room = editTextRoom.text.toString()
            bedroom = editTextBedroom.text.toString()
            priceMin = editTextPriceMin.text.toString()
            priceMax = editTextPriceMax.text.toString()
        }
        viewModel.allProperty.observe(viewLifecycleOwner, Observer { properties ->
            filterProperties(properties as MutableList<Property>)
        })
    }

    private fun filterProperties(properties: MutableList<Property>) {
        filterList = properties
        if (surfaceMin.isNotEmpty() && surfaceMax.isNotEmpty() && filterList.isNotEmpty()) {
            surfaceFilter()
        }
        if (room.isNotEmpty() && filterList.isNotEmpty()) {
            roomFilter()
        }
        if (bedroom.isNotEmpty() && filterList.isNotEmpty()) {
            bedroomFilter()
        }
        if (priceMin.isNotEmpty() && priceMax.isNotEmpty() && filterList.isNotEmpty()) {
            priceFilter()
        }
        if (!typeChoice.isNullOrEmpty() && filterList.isNotEmpty()) {
            typeFilter()
        }
        if (!assetList.isNullOrEmpty() && filterList.isNotEmpty()) {
            assetFilter()
        }
        if (!interestList.isNullOrEmpty() && filterList.isNotEmpty()) {
            interestFilter()
        }
        Toast.makeText(activity, filterList[0].type, Toast.LENGTH_SHORT).show()
    }

    private fun surfaceFilter() {
        for (i in 0 until filterList.size) {
            if (!filterList[i].surface.isNullOrEmpty()) {
                if (surfaceMin.toInt() > filterList[i].surface?.toInt()!! || filterList[i].surface?.toInt()!! > surfaceMax.toInt()) {
                    filterList.remove(filterList[i])
                }
            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    private fun roomFilter() {
        for (i in 0 until filterList.size) {
            if (!filterList[i].room.isNullOrEmpty()) {
                if (room.toInt() > filterList[i].room?.toInt()!!) {
                    filterList.remove(filterList[i])
                }
            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    private fun bedroomFilter() {
        for (i in 0 until filterList.size) {
            if (!filterList[i].bedroom.isNullOrEmpty()) {
                if (bedroom.toInt() > filterList[i].bedroom?.toInt()!!) {
                    filterList.remove(filterList[i])
                }
            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    private fun priceFilter() {
        for (i in 0 until filterList.size) {
            if (!filterList[i].price.isNullOrEmpty()) {
                if (priceMin.toInt() > filterList[i].price?.toInt()!! || filterList[i].price?.toInt()!! > priceMax.toInt()) {
                    filterList.remove(filterList[i])
                }
            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    private fun typeFilter() {
        for (i in 0 until filterList.size) {
            if (!filterList[i].type.isNullOrEmpty()) {
                if (typeChoice.toString() != filterList[i].type) {
                    filterList.remove(filterList[i])
                }
            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    private fun assetFilter() {
        var exist = true
        for (i in 0 until filterList.size) {
            if (!filterList[i].asset.isNullOrEmpty()) {
                for (j in 0..(filterList[i].asset?.size!!)) {
                    if (!assetList.contains(filterList[i].asset!![j])) {
                        exist = false
                    }
                }
                if (!exist) {
                    filterList.remove(filterList[i])
                }

            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    private fun interestFilter() {
        var exist = true
        for (i in 0 until filterList.size) {
            if (!filterList[i].pointOfInterest.isNullOrEmpty()) {
                for (j in 0..(filterList[i].pointOfInterest?.size!!)) {
                    if (!interestList.contains(filterList[i].pointOfInterest!![j])) {
                        exist = false
                    }
                }
                if (!exist) {
                    filterList.remove(filterList[i])
                }
            } else {
                filterList.remove(filterList[i])
            }
        }
    }

    companion object {
        fun newInstance() = FilterFragment()
    }
}
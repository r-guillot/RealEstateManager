package com.openclassrooms.realestatemanager.filter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.utils.FragmentCallback
import com.openclassrooms.realestatemanager.databinding.FragmentFilterBinding
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.RealEstateApplication

class FilterFragment : BottomSheetDialogFragment() {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }
    private val filterViewModel: FilterViewModel by viewModels()
    private lateinit var fragmentCallback: FragmentCallback
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
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(layoutInflater)

        viewModel.allProperty.observe(viewLifecycleOwner, Observer { properties ->
            getPropertyList(properties as MutableList<Property>)
        })
        getChipType()
        onNoFilterButtonClick()
        onFilterButtonClick()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallback = context as FragmentCallback
    }

    private fun getPropertyList(properties: MutableList<Property>) {
        filterList = properties
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

    private fun onNoFilterButtonClick(){
        binding.buttonNoFilter.setOnClickListener(View.OnClickListener {
            viewModel.allProperty.observe(viewLifecycleOwner, Observer { properties ->
                getNoFilteredList(properties as MutableList<Property>)
            })
        })
    }

    private fun getNoFilteredList(properties: MutableList<Property>){
        fragmentCallback.onPropertyFiltered(properties)
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()

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
            filterList = filterViewModel.filterSurface(filterList, surfaceMin, surfaceMax)
        }
        if (room.isNotEmpty() && filterList.isNotEmpty()) {
            filterList = filterViewModel.filterRoom(filterList, room)
        }
        if (bedroom.isNotEmpty() && filterList.isNotEmpty()) {
            filterList = filterViewModel.filterBedroom(filterList, bedroom)
        }
        if (priceMin.isNotEmpty() && priceMax.isNotEmpty() && filterList.isNotEmpty()) {
            filterList = filterViewModel.filterPrice(filterList, priceMin, priceMax)
        }
        if (!typeChoice.isNullOrEmpty() && filterList.isNotEmpty()) {
            filterList = filterViewModel.filterType(filterList, typeChoice)
        }
        if (!assetList.isNullOrEmpty() && filterList.isNotEmpty()) {
            filterList = filterViewModel.filterAsset(filterList, assetList)
        }
        if (!interestList.isNullOrEmpty() && filterList.isNotEmpty()) {
            filterList = filterViewModel.filterInterest(filterList, interestList)
        }
        Toast.makeText(activity, filterList.size.toString(), Toast.LENGTH_SHORT).show()
        fragmentCallback.onPropertyFiltered(filterList)
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }
}
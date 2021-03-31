//package com.openclassrooms.realestatemanager.propertylist
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
//import androidx.navigation.NavController
//import androidx.navigation.fragment.NavHostFragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.openclassrooms.realestatemanager.R
//import com.openclassrooms.realestatemanager.databinding.FragmentMasterBinding
//import com.openclassrooms.realestatemanager.detail.DetailFragment
//import com.openclassrooms.realestatemanager.main.PropertyListViewModel
//import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
//import com.openclassrooms.realestatemanager.model.Property
//import com.openclassrooms.realestatemanager.newproperty.NewPropertyActivity
//import com.openclassrooms.realestatemanager.room.RealEstateApplication
//
//class MasterFragment : Fragment() {
//    private lateinit var binding: FragmentMasterBinding
//    private lateinit var navController: NavController
//    private lateinit var propertyRecycleView: RecyclerView
//    private var mAdapter: PropertyListAdapter? = null
//    private lateinit var properties: List<Property>
//    private lateinit var adapter: PropertyListAdapter
//
//    private val viewModel: PropertyListViewModel by viewModels {
//        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View {
//        binding = FragmentMasterBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        navController = getNavController()
////        navController = Navigation.findNavController(requireActivity(), R.id.core_fragment_container)
//        getProperties()
//        goToNewPropertyActivity()
//    }
//
//    private fun getNavController(): NavController {
//        val fragment: Fragment? = activity?.supportFragmentManager?.findFragmentById(R.id.core_fragment_container)
//        check(fragment is NavHostFragment) {
//            ("Activity $this does not have a NavHostFragment")
//        }
//        return fragment.navController
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // true only in landscape
//        if (binding.detailFragmentContainer != null) {
//            childFragmentManager.beginTransaction()
//                    .replace(binding.detailFragmentContainer!!.id, DetailFragment())
//                    .commit()
//        }
//    }
//
//    private fun getProperties() {
//        activity?.let {
//            viewModel.allProperty.observe(it, Observer { properties ->
//                configureRecycleView(properties)
//                Log.d("RV", "getProperties: $properties")
//            })
//        }
//    }
//
//    private fun configureRecycleView(propertyList: List<Property>) {
//        binding.propertyRecyclerView.apply {
//            layoutManager = LinearLayoutManager(activity)
//            adapter = PropertyListAdapter(propertyList) { property -> itemClicked(property) }
//        }
//    }
//
//    private fun itemClicked(property: Property) {
//        Toast.makeText(activity, "Clicked: ${property.id}", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun goToNewPropertyActivity() {
//        binding.fabNewProperty.setOnClickListener {
//            startActivity(Intent(activity, NewPropertyActivity::class.java))
//        }
//    }
//
//    companion object {
//        fun newInstance(): MasterFragment {
//            return MasterFragment()
//        }
//    }
//}
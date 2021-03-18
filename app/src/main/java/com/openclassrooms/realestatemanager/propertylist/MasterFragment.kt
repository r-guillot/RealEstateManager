package com.openclassrooms.realestatemanager.propertylist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMasterBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.NewProperty
import com.openclassrooms.realestatemanager.room.RealEstateApplication


class MasterFragment: Fragment() {
    private lateinit var binding: FragmentMasterBinding
    private lateinit var navController: NavController
    private lateinit var propertyRecycleView : RecyclerView
    private var adapter: propertyListAdapter? = null

    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((activity?.application as RealEstateApplication).propertyRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMasterBinding.inflate(layoutInflater)

        propertyRecycleView = binding.propertyRecyclerView as RecyclerView
        propertyRecycleView.layoutManager = LinearLayoutManager(context)

        getProperties()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = getNavController()
//        navController = Navigation.findNavController(requireActivity(), R.id.core_fragment_container)
//        getProperties()
        goToNewPropertyActivity()
    }

    private fun getNavController(): NavController {
        val fragment: Fragment? = activity?.supportFragmentManager?.findFragmentById(R.id.core_fragment_container)
        check(fragment is NavHostFragment) {
            ("Activity $this does not have a NavHostFragment")
        }
        return fragment.navController
    }

    override fun onResume() {
        super.onResume()
        // true only in landscape
        if (binding.detailFragmentContainer != null) {
            childFragmentManager.beginTransaction()
                    .replace(binding.detailFragmentContainer!!.id, DetailFragment())
                    .commit()
        }
    }

    private fun getProperties() {
        activity?.let {
            viewModel.allProperty.observe(it, Observer { properties ->
                updateUI(properties)
            })
        }
    }


    private fun updateUI(propertyList: List<Property>) {
        adapter = propertyListAdapter(propertyList)
        //set the adapter to out RecyclerView
        propertyRecycleView.adapter = adapter
    }

    private fun goToNewPropertyActivity(){
        binding.fabNewProperty?.setOnClickListener {
            startActivity(Intent(activity, NewProperty::class.java))
        }
    }

    private inner class PropertyHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {

        private lateinit var property: Property

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

        fun bind(property: Property) {
            this.property = property
        }


    }

    private inner class propertyListAdapter(var list: List<Property>) : RecyclerView.Adapter<PropertyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyHolder {
            val view = layoutInflater.inflate(R.layout.item_property, parent, false)
            return PropertyHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: PropertyHolder, position: Int) {
            val property = list[position]
            holder.bind(property)
        }

    }

    companion object {
        fun newInstance() : MasterFragment {
            return MasterFragment()
        }
    }
}
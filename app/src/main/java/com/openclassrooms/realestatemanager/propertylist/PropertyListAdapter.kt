package com.openclassrooms.realestatemanager.propertylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import com.openclassrooms.realestatemanager.model.Property

class PropertyListAdapter(private val propertyList: List<Property>, private val twoPane: Boolean, private val onClickListener: (Property) ->
Unit): RecyclerView.Adapter<PropertyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding = ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PropertyViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(viewHolder: PropertyViewHolder, position: Int) {
        viewHolder.updatePropertyInfo(propertyList[position])
    }

    override fun getItemCount() = propertyList.size


}
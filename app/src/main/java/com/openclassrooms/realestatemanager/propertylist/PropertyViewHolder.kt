package com.openclassrooms.realestatemanager.propertylist

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import com.openclassrooms.realestatemanager.model.Property

class PropertyViewHolder(private var binding: ItemPropertyBinding, private val onClickListener: (Property) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun updatePropertyInfo(property: Property) {
        val imageUri: Uri? = property.photo?.get(0)

        binding.apply {
            imageUri?.let { imageViewProperty.setImageURI(it) }
            textViewNumberRooms.text = property.room.toString()
            textViewPropertyPlace.text = property.address
            textViewPropertyPrice.text = property.price.toString()
            textViewPropertySurface.text = property.surface.toString()
            textViewPropertyType.text = property.type
            textViewPropertyOnlineDate.text = property.arrivalDate
        }

        itemView.setOnClickListener { onClickListener(property) }
    }

}

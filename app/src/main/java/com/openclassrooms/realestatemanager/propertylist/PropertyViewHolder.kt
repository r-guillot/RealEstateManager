package com.openclassrooms.realestatemanager.propertylist

import android.net.Uri
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import com.openclassrooms.realestatemanager.model.Property

class PropertyViewHolder(private var binding: ItemPropertyBinding, private val onClickListener: (Property) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun updatePropertyInfo(property: Property) {
        val imageUri: Uri? = property.photo?.get(0)

        binding.apply {
            imageUri?.let { imageViewProperty.setImageURI(it) }
            if (property.sold) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imageViewProperty.foreground = ResourcesCompat.getDrawable(binding.root.resources, R.drawable.ic_sold, null)
                }
                textViewPropertySoldDate.visibility = View.VISIBLE
                textViewPropertySoldDate.text = property.soldDate
            }
            textViewNumberRooms.text = checkIfNull(property.room.toString(), " rooms")
            textViewPropertyPlace.text = checkIfNull(property.address.toString(), "")
            textViewPropertyPrice.text = checkIfNull(property.price.toString(), " $")
            textViewPropertySurface.text = checkIfNull(property.surface.toString(), " m²")
            textViewPropertyType.text = checkIfNull(property.type.toString(), "")
            textViewPropertyOnlineDate.text = property.arrivalDate
        }

        itemView.setOnClickListener { onClickListener(property) }
    }

    private fun checkIfNull(info: String, unit: String): String {
        return if (info.contains("null")) {
            "no info"
        } else {
            info + unit
        }
    }
}

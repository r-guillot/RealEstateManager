package com.openclassrooms.realestatemanager.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityPropertyListBinding
import com.openclassrooms.realestatemanager.newproperty.NewProperty
import com.openclassrooms.realestatemanager.room.RealEstateApplication


class PropertyListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPropertyListBinding
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_list)

        configureBinding()
        getProperties()
        goToNewPropertyActivity()
    }

    private fun configureBinding() {
        binding = ActivityPropertyListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


    private fun getProperties() {
        viewModel.allProperty.observe(this, Observer { properties ->
            properties.let { Toast.makeText(this,"ok",Toast.LENGTH_SHORT).show() }
        })
    }

    private fun goToNewPropertyActivity(){
        binding.fabNewProperty.setOnClickListener {
        startActivity(Intent(this, NewProperty::class.java))
        }
    }
}
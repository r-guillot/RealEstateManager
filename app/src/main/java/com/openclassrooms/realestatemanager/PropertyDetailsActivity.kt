package com.openclassrooms.realestatemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.openclassrooms.realestatemanager.databinding.ActivityPropertyDetailsBinding

private lateinit var binding: ActivityPropertyDetailsBinding

class PropertyDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        configureBinding()
    }

    private fun configureBinding() {
        binding = ActivityPropertyDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


}
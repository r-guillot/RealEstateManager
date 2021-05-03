package com.openclassrooms.realestatemanager.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityFullScreenImageBinding

class FullScreenImage : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        configureBinding()
        displayImage()
    }

    private fun configureBinding() {
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun displayImage() {
        val imageUri: String? = intent.getStringExtra("image")
        binding.imageViewFullscreen.setImageURI(imageUri?.toUri())
    }
}
package com.openclassrooms.realestatemanager.newproperty

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.URIPathHelper
import com.openclassrooms.realestatemanager.databinding.ActivityNewPropertyBinding
import com.openclassrooms.realestatemanager.main.PropertyListActivity
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.RealEstateApplication

class NewProperty : AppCompatActivity() {
    private lateinit var binding:ActivityNewPropertyBinding
    private val viewModel: NewPropertyViewModel by viewModels {
        NewPropertyViewModelFactory((application as RealEstateApplication).propertyRepository) }
    private var typeChoice: CharSequence? = null
    private val assetList = mutableListOf<CharSequence?>()
    private val interestList= mutableListOf<CharSequence?>()
    private val photoList= mutableListOf<Uri?>()
    private var videoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_property)

        configureBinding()
        validation()
    }

    private fun configureBinding() {
        binding = ActivityNewPropertyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getChipType() {
        binding.chipGroupType.setOnCheckedChangeListener { chipGroup, checkedId ->
            typeChoice = chipGroup.findViewById<Chip>(checkedId)?.text
            Toast.makeText(chipGroup.context, typeChoice ?: "No Choice", Toast.LENGTH_LONG).show()
        }
    }

    private fun getChipAsset(){
        val ids = binding.chipGroupAsset.checkedChipIds

        binding.chipGroupAsset.forEach { child ->
            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                ids.forEach { id ->
                    assetList.add(binding.chipGroupAsset.findViewById<Chip>(id).text)
                }

                val text = if (assetList.isNotEmpty()) {
                    assetList.joinToString(", ")
                } else {
                    "No Asset"
                }

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getChipInterest(){
        val ids = binding.chipGroupInterest.checkedChipIds

        binding.chipGroupInterest.forEach { child ->
            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                ids.forEach { id ->
                    interestList.add(binding.chipGroupInterest.findViewById<Chip>(id).text)
                }

                val text = if (interestList.isNotEmpty()) {
                    interestList.joinToString(", ")
                } else {
                    "No Asset"
                }

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val clipData: ClipData? = result.data?.clipData
            if (clipData != null) {
                for (i in 0..(clipData.itemCount)){
                    val imageUri:Uri = clipData.getItemAt(i).uri
                    photoList.add(imageUri)
                }
            } else {
                val uri: Uri? = result.data?.data
                uri?.let { photoList.add(it) }
            }
        }
    }

    private fun getPhotosFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        resultLauncher.launch(intent)
    }

    private var resultLauncherVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {

                val uriPathHelper = URIPathHelper()
                videoUri = result.data?.data?.let { uriPathHelper.getPath(this, it) }
            }
        }
    }

    private fun getVideoFromGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        resultLauncherVideo.launch(intent)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == 1){
//            val clipData: ClipData? = data?.clipData
//            if (clipData != null) {
//                for (i in 0..(clipData.itemCount)){
//                    val imageUri:Uri = clipData.getItemAt(i).uri
//                    photoList.add(imageUri)
//                }
//            } else {
//                val uri: Uri? = data?.data
//                uri?.let { photoList.add(it) }
//            }
//        }
//    }


    private fun validation(){
        binding.buttonValidate.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage("Kotlin message")
                this.setPositiveButton("Yes !") { _, _ ->
                    createNewProperty()
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
    }

    private fun createNewProperty() {
        getChipType()
        getChipAsset()
        getChipInterest()
        val address= binding.editTextAddress.text.toString()
        val surface= binding.editTextSurface.text.toString().toInt()
        val rooms= binding.editTextRoom.text.toString().toInt()
        val bedrooms= binding.editTextBedroom.text.toString().toInt()
        val bathrooms= binding.editTextBathroom.text.toString().toInt()
        val price= binding.editTextPrice.text.toString().toInt()
        val description= binding.editTextDescription.text.toString()
        val asset= assetList
        val interest= interestList
        val type= typeChoice.toString()
        val photo = photoList
        val video = videoUri

        var property = Property(1, type, price, surface, rooms, bedrooms, bathrooms, description,
        photo, address, asset, interest, sold = false, soldDate = null, arrivalDate = "todo","todo")
        viewModel.insertNewProperty(property)
        startActivity(Intent(this, PropertyListActivity::class.java))
    }
}
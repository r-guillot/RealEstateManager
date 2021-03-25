package com.openclassrooms.realestatemanager.newproperty

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.forEach
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.ActivityNewPropertyBinding
import com.openclassrooms.realestatemanager.main.CoreActivity
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.RealEstateApplication
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class NewProperty : AppCompatActivity() {
    private lateinit var binding: ActivityNewPropertyBinding
    private val viewModel: NewPropertyViewModel by viewModels {
        NewPropertyViewModelFactory((application as RealEstateApplication).propertyRepository)
    }
    private var typeChoice: CharSequence? = null
    private var assetList = mutableListOf<String>()
    private var interestList = mutableListOf<String>()
    private var photoListUri = mutableListOf<Uri>()
    private var photoListBitmap = mutableListOf<Bitmap>()
    private var videoUri: Uri? = null
    var imageFilePath: String? = null
    private lateinit var photoURI: Uri
    private lateinit var agent: String

    private val TAG = NewProperty::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_property)

        configureBinding()
        getChipType()
//        getChipAsset()
        getChipInterest()
        getOrTakePhoto()
        getVideo()
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

    private fun getChipAsset() {
        val ids = binding.chipGroupAsset.checkedChipIds

        for (id in ids) {
            val chip: Chip = binding.chipGroupAsset.findViewById(id)
            assetList.add(chip.text.toString())
                    Toast.makeText(this, assetList.toString(), Toast.LENGTH_SHORT).show()
        }

//        binding.chipGroupAsset.forEach { child ->
//            (child as? Chip)?.setOnCheckedChangeListener { chipGroup, checkedId ->
//                ids.forEach { id ->
//                    Log.d(TAG, "getChipAsset: $id")
//                    assetList.add(chipGroup.findViewById<Chip>(id).text.toString())
//                    Toast.makeText(this, assetList.toString(), Toast.LENGTH_SHORT).show()
//                }
//
//                val text = if (assetList.isNotEmpty()) {
//                    assetList.joinToString(", ")
//                } else {
//                    "No Asset"
//                }
//
//                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun getChipInterest() {
        val ids = binding.chipGroupInterest.checkedChipIds

        for (id in ids) {
            val chip: Chip = binding.chipGroupInterest.findViewById(id)
            interestList.add(chip.text.toString())
            Toast.makeText(this, interestList.toString(), Toast.LENGTH_SHORT).show()
        }

//        binding.chipGroupInterest.forEach { child ->
//            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
//                ids.forEach { id ->
//                    interestList.add(binding.chipGroupInterest.findViewById<Chip>(id).text.toString())
//                }
//
//                val text = if (interestList.isNotEmpty()) {
//                    interestList.joinToString(", ")
//                } else {
//                    "No Asset"
//                }
//
//                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val clipData: ClipData? = result.data?.clipData
            if (clipData != null) {
                for (i in 0 until (clipData.itemCount)) {
                    val imageUri: Uri = clipData.getItemAt(i).uri
                    photoListUri.add(imageUri)

//                    val `is`: InputStream? = contentResolver.openInputStream(imageUri)
//                    val bitmap = BitmapFactory.decodeStream(`is`)
//                    photoListBitmap.add(bitmap)
                }
            } else {
                val uri: Uri? = result.data?.data
                uri?.let { photoListUri.add(it)
//                    val `is`: InputStream? = contentResolver.openInputStream(uri)
//                    val bitmap = BitmapFactory.decodeStream(`is`)
//                    photoListBitmap.add(bitmap)
                }
            }
            Log.d(TAG, "photoList result $photoListUri + bitmap $photoListBitmap")
            displaySelectedPhotoInViewPager()
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

    private fun displaySelectedPhotoInViewPager() {
        Log.d(TAG, "photoList display $photoListUri")
        if (photoListUri.size > 0) {
            binding.viewPager.visibility = View.VISIBLE
            val adapter = ViewPagerAdapter(photoListUri, this)
            binding.viewPager.adapter = adapter
        }
    }

    private var resultCapturePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            photoListUri.add(photoURI)

//            val `is`: InputStream? = contentResolver.openInputStream(photoURI)
//            val bitmap = BitmapFactory.decodeStream(`is`)
//            photoListBitmap.add(bitmap)
            Log.d(TAG, "uri: $photoURI")
            Log.d(TAG, "photolist after capture: $photoListUri ")
            displaySelectedPhotoInViewPager()
        }
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            null
        }
        // Continue only if the File was successfully created
        photoFile?.also {
            photoURI = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    it)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            resultCapturePhoto.launch(cameraIntent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //        imageFilePath = image.absolutePath
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }


    private fun getOrTakePhoto() {
        binding.buttonAddPhoto.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(resources.getString(R.string.add_photo_title))
                setMessage(resources.getString(R.string.photo_message))
                this.setPositiveButton(resources.getString(R.string.from_gallery)) { _, _ ->
                    getPhotosFromGallery()
                }
                setNegativeButton(resources.getString(R.string.shoot_photo)) { _, _ ->
                    capturePhoto()
                }
                setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
    }

    private var resultLauncherVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {

                val uri: Uri? = result.data?.data
                uri?.let { videoUri = it }

                getThumbVideo(this, videoUri)
            }
        }
    }

    private fun getThumbVideo(context: Context?, videoUri: Uri?) {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, videoUri)
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        binding.imageViewVideo.visibility = View.VISIBLE
        binding.imageViewVideo.setImageBitmap(bitmap)
    }

    private fun getVideoFromGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        resultLauncherVideo.launch(intent)
    }

    private fun getVideo() {
        binding.buttonAddVideo.setOnClickListener {
            getVideoFromGallery()
        }
    }


    private fun validation() {
        binding.buttonValidate.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter the name of Agent"
            input.inputType = InputType.TYPE_CLASS_TEXT

            AlertDialog.Builder(this).apply {
                setTitle("Kotlin message")
                setView(input)
                this.setPositiveButton("Yes !") { dialog, _ ->
                    val text = input.text?.toString()
                    text?.let {
                        agent = it
                        createNewProperty()
                    } ?: kotlin.run {
                        Toast.makeText(context, "You have to give your name", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
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
        Log.d(TAG, "createNewProperty: ")
        getChipAsset()
        val currentDate = Utils.getTodayDate()

        val id = (System.currentTimeMillis() / 1000).toInt()
        val address = binding.editTextAddress.text.toString()
        val surface = checkIfEmpty(binding.editTextSurface.text.toString())
        val rooms = checkIfEmpty(binding.editTextRoom.text.toString())
        val bedrooms = checkIfEmpty(binding.editTextBedroom.text.toString())
        val bathrooms = checkIfEmpty(binding.editTextBathroom.text.toString())
        val price = checkIfEmpty(binding.editTextPrice.text.toString())
        val description = binding.editTextDescription.text.toString()
        val asset = assetList
        val interest = interestList
        val type = typeChoice.toString()
        val photo = photoListBitmap
        val video = videoUri

        val property = Property(id, type, price, surface, rooms, bedrooms, bathrooms, description,
                photo, video, address, asset, interest, sold = false, soldDate = null, arrivalDate = currentDate, agent)
        viewModel.insertNewProperty(property)
        startActivity(Intent(this, CoreActivity::class.java))
    }

    private fun checkIfEmpty(string: String): Int? {
        return if(string.isEmpty()) {
            null
        } else string.toInt()
    }
}
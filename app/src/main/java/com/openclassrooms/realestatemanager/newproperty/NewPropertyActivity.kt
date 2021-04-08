package com.openclassrooms.realestatemanager.newproperty

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.NotificationHelper
import com.openclassrooms.realestatemanager.propertylist.PropertyListActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.ActivityNewPropertyBinding
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.room.RealEstateApplication
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class NewPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPropertyBinding
    private val viewModel: NewPropertyViewModel by viewModels {
        NewPropertyViewModelFactory((application as RealEstateApplication).propertyRepository)
    }
    private var typeChoice: CharSequence? = null
    private var assetList = mutableListOf<String>()
    private var interestList = mutableListOf<String>()
    private var photoListUri = mutableListOf<Uri>()
    private var videoUri: Uri? = null
    var imageFilePath: String? = null
    private lateinit var photoURI: Uri
    private lateinit var agent: String
    private var propertyLat: Double = 40.755986
    private var propertyLong: Double = -73.984712

    private val notification: NotificationHelper = NotificationHelper()

    private val TAG = NewPropertyActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureBinding()
        notification.createNotificationChannel(this)
        getChipType()
        getOrTakePhoto()
        getVideo()
        validation()
    }

    private fun configureBinding() {
        binding = ActivityNewPropertyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    //getChipType(), getChipAsset() et getChipInterest() get the type, the interests and the assets
    // which selected by the agent for the creation of the property
    private fun getChipType() {
        binding.chipGroupType.setOnCheckedChangeListener { chipGroup, checkedId ->
            typeChoice = chipGroup.findViewById<Chip>(checkedId)?.text
            Toast.makeText(chipGroup.context, typeChoice ?: "No Choice", Toast.LENGTH_LONG).show()
        }
    }

    private fun getChipAsset() {
        for (id in binding.chipGroupAsset.checkedChipIds) {
            val chip: Chip = binding.chipGroupAsset.findViewById(id)
            assetList.add(chip.text.toString())
        }
    }

    private fun getChipInterest() {
        for (id in binding.chipGroupInterest.checkedChipIds) {
            val chip: Chip = binding.chipGroupInterest.findViewById(id)
            interestList.add(chip.text.toString())
        }
    }

    //ActivityResult after getting pictures in gallery(one or multiple) and display them in ViewPager
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val clipData: ClipData? = result.data?.clipData
            if (clipData != null) {
                for (i in 0 until (clipData.itemCount)) {
                    val imageUri: Uri = clipData.getItemAt(i).uri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //add permission for long time use
                        contentResolver.takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        photoListUri.add(imageUri)
                    }
                }
            } else {
                val uri: Uri? = result.data?.data
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (uri != null) {
                        //add permission for long time use
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        photoListUri.add(uri)
                    }
                }
            }
            displaySelectedPhotoInViewPager()
        }
    }


    private fun getPhotosFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        resultLauncher.launch(intent)
    }

    private fun displaySelectedPhotoInViewPager() {
        Log.d(TAG, "photoList display $photoListUri")
        if (photoListUri.size > 0) {
            binding.viewPager.visibility = View.VISIBLE
            val adapter = ViewPagerAdapter(photoListUri, this, 0.5f)
            binding.viewPager.adapter = adapter
        }
    }

    //ActivityResult after shoot a photo, add it in photoListUri and display it in ViewPager
    private var resultCapturePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            photoListUri.add(photoURI)
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

    //Create image after taking a photo
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

    //AlertDialog for choosing the way to add the photo
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

    ////ActivityResult after get a video
    private var resultLauncherVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {

                val uri: Uri? = result.data?.data
                uri?.let { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    videoUri = it }

                getThumbVideo(this, videoUri)
            }
        }
    }

    //Create a thumbnail of the recovered video and display it under the ViewPager
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
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        resultLauncherVideo.launch(intent)
    }

    private fun getVideo() {
        binding.buttonAddVideo.setOnClickListener {
            getVideoFromGallery()
        }
    }

    //AlertDialog after button pressed, we ask for the name of the agent,
    // if he don't give it, he can't create a new Property
    private fun validation() {
        binding.buttonValidate.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter the name of Agent"
            input.inputType = InputType.TYPE_CLASS_TEXT

            AlertDialog.Builder(this).apply {
                setTitle("Kotlin message")
                setView(input)
                this.setPositiveButton("Yes !") { dialog, _ ->
                    val text = input.text.toString()
                    if (text.isNotEmpty()) {
                        agent = text
                        createNewProperty()
                    } else {
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

    //Create a new Property
    private fun createNewProperty() {
        getChipAsset()
        getChipInterest()
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
        val photo = photoListUri
        val video = videoUri

        if (address.isNotEmpty()){getCoordinatesFromAddress(address)}

        val property = Property(id, type, price, surface, rooms, bedrooms, bathrooms, description,
                photo, video, address, asset, interest, sold = false, soldDate = null, arrivalDate = currentDate, agent, propertyLat, propertyLong)
        Log.d(TAG, "createNewProperty: $property")
        viewModel.insertNewProperty(property)
        notification.showNotification()
        startActivity(Intent(this, PropertyListActivity::class.java))
    }

    //Use for check if the editText with the int is empty or not, return null if is it
    private fun checkIfEmpty(string: String): Int? {
        return if (string.isEmpty()) {
            null
        } else string.toInt()
    }

    private fun getCoordinatesFromAddress(address: String) {
        val coord = Geocoder(this);
        val addresses = coord.getFromLocationName(address, 1)
        propertyLat = addresses[0].latitude
        propertyLong = addresses[0].longitude
    }

}
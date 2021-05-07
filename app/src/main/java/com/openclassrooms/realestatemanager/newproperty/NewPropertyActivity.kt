package com.openclassrooms.realestatemanager.newproperty

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityNewPropertyBinding
import com.openclassrooms.realestatemanager.detail.PropertyDetailFragment
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.propertylist.PropertyListActivity
import com.openclassrooms.realestatemanager.room.RealEstateApplication
import com.openclassrooms.realestatemanager.utils.NotificationHelper
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.ViewHelper
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
    private var descriptionPhotoList = mutableListOf<String>()
    private var photoListUri = mutableListOf<Uri>()
    private var videoUri: Uri? = null
    private var imageFilePath: String? = null
    private lateinit var photoURI: Uri
    private lateinit var agent: String
    private var edit: Boolean = false
    private var id: String? = null
    private var propertyLat: Double = 40.755986
    private var propertyLong: Double = -73.984712
    private var soldState: Boolean = false
    private var soldDate: String? = null
    private lateinit var propertyEdited: Property
    private val notification: NotificationHelper = NotificationHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        notification.createNotificationChannel(this)
        getChipType()
        getOrTakePhoto()
        getVideo()
        validation()

        //check if we receive an intent with extra, if it's yes this is an update so edit become true
        if (intent.getStringExtra(PropertyDetailFragment.ARG_ITEM_ID) != null) {
            id = intent.getStringExtra(PropertyDetailFragment.ARG_ITEM_ID)
            edit = true
            viewModel.allProperty.observe(this, { properties ->
                getProperties(properties)
            })
        }
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

    private fun getChipDescription() {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            descriptionPhotoList.add(chip.text.toString())
        }
    }

    //ActivityResult after getting pictures in gallery(one or multiple) and display them in ViewPager
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

    //Get photos from gallery and display it in view pager
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
        if (photoListUri.size > 0) {
            binding.viewPager.visibility = View.VISIBLE
            val adapter = ViewPagerAdapter(photoListUri, this, 0.5f, null)
            binding.viewPager.adapter = adapter
            addDescriptionForPhoto()
        }
    }

    //ActivityResult after shoot a photo, add it in photoListUri and display it in ViewPager
    private var resultCapturePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoListUri.add(photoURI)
            displaySelectedPhotoInViewPager()
        }
    }

    //Taking a photo with camera
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
                    this, BuildConfig.APPLICATION_ID + ".fileprovider", it)
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
        val image = File.createTempFile(imageFileName,  /* prefix */".jpg",  /* suffix */
                storageDir /* directory */)
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
                setNeutralButton(resources.getString(R.string.delete_photo)) { dialog, _ ->
                    photoListUri.removeAll(photoListUri)
                    binding.viewPager.visibility = View.GONE
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
    }

    //add description for added photo
    private fun addDescriptionForPhoto() {
        binding.textInputLayoutImageDescription.visibility = View.VISIBLE
        binding.textInputEditTextImageDescription.setOnEditorActionListener(OnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val description = textView.text.toString().trim { it <= ' ' }
                if (description.isNotEmpty()) {
                    addChipToGroup(description, binding.chipGroup)
                    binding.textInputEditTextImageDescription.setText("")
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun addChipToGroup(text: String, chipGroup: ChipGroup) {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip))
        chip.text = text
        chipGroup.addView(chip)
        chip.isCloseIconVisible
        chip.setOnCloseIconClickListener { chipGroup.removeView(chip) }
    }

    //ActivityResult after get a video
    private var resultLauncherVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    videoUri = it
                }
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
            AlertDialog.Builder(this).apply {
                setTitle(resources.getString(R.string.add_video_title))
                this.setPositiveButton(resources.getString(R.string.video_gallery)) { _, _ ->
                    getVideoFromGallery()
                }
                setNegativeButton(resources.getString(R.string.delete_video)) { dialog, _ ->
                    videoUri = null
                    binding.imageViewVideo.visibility = View.GONE
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
    }

    private fun checkIfPropertyIsSold() {
        soldState = binding.switchSold.isChecked
        if (soldState) {
            soldDate = Utils.getTodayDate()
        }
    }

    //AlertDialog after button pressed, we ask for the name of the agent,
    // if he don't give it, he can't create a new Property
    private fun validation() {
        binding.buttonValidate.setOnClickListener {
            val input = EditText(this)
            input.hint = getString(R.string.enter_name)
            input.inputType = InputType.TYPE_CLASS_TEXT

            if (!photoListUri.isNullOrEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_alertDialog))
                    if (!edit) {
                        setView(input)
                    }
                    this.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        val text = input.text.toString()
                        if (text.isNotEmpty()) {
                            agent = text
                            createNewProperty(false)
                        } else {
                            if (edit) {
                                createNewProperty(true)
                            } else {
                                Toast.makeText(this@NewPropertyActivity, getString(R.string.enter_name_error), Toast.LENGTH_LONG).show()
                            }
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            } else {
                Toast.makeText(this, getString(R.string.photo_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    //Create a new Property
    private fun createNewProperty(update: Boolean) {
        getChipAsset()
        getChipInterest()
        getChipDescription()
        checkIfPropertyIsSold()
        val currentDate = Utils.getTodayDate()

        val id = (System.currentTimeMillis() / 1000).toInt()
        val address = binding.editTextAddress.text.toString()
        val surface = binding.editTextSurface.text.toString()
        val rooms = binding.editTextRoom.text.toString()
        val bedrooms = binding.editTextBedroom.text.toString()
        val bathrooms = binding.editTextBathroom.text.toString()
        val price = binding.editTextPrice.text.toString()
        val description = binding.editTextDescription.text.toString()
        val asset = assetList
        val interest = interestList
        val type = typeChoice.toString()
        val photo = photoListUri
        val photoDescription = descriptionPhotoList
        val video = videoUri
        val sold = soldState
        if (address.isNotEmpty()) {
            getCoordinatesFromAddress(address)
        }

        //Check if it's a creation or an update
        if (!update) {
            val property = Property(id, type, price, surface, rooms, bedrooms, bathrooms, description,
                    photo, photoDescription, video, address, asset, interest, sold, soldDate, currentDate, agent, propertyLat, propertyLong)
            viewModel.insertNewProperty(property)
            notification.showNotification()
        } else {
            val propertyEdit = Property(propertyEdited.id, type, price, surface, rooms, bedrooms, bathrooms, description,
                    photo, photoDescription, video, address, asset, interest, sold, soldDate, propertyEdited.arrivalDate, propertyEdited.agent, propertyLat, propertyLong)
            viewModel.updateProperty(propertyEdit)
        }
        startActivity(Intent(this, PropertyListActivity::class.java))
    }

    //get latitude and longitude of the property using its address
    private fun getCoordinatesFromAddress(address: String) {
        val addresses = viewModel.getCord(address, this)
        propertyLat = addresses?.get(0)!!.latitude
        propertyLong = addresses[0].longitude
    }

    private fun getProperties(propertyList: List<Property>) {
        val property = propertyList.find { it.id == id?.toInt() }
        property?.let { populateWithInfo(it) }
    }

    //populate editText and chip if is it an update
    private fun populateWithInfo(property: Property) {
        this.propertyEdited = property
        binding.apply {
            editTextAddress.setText(property.address)
            editTextSurface.setText(property.surface)
            editTextRoom.setText(property.room)
            editTextBedroom.setText(property.bedroom)
            editTextBathroom.setText(property.bathroom)
            editTextPrice.setText(property.price)
            editTextDescription.setText(property.description)

            if (property.photo?.get(0).toString().contains("null") || property.photo?.get(0).toString().isBlank()) {
                photoListUri.removeAll(photoListUri)
                binding.viewPager.visibility = View.GONE
            } else {
                photoListUri = property.photo!!
                displaySelectedPhotoInViewPager()
            }
            if (!property.video.toString().contains("null")) {
                property.video?.let { getThumbVideo(this@NewPropertyActivity, property.video) }
            }
            populateChips(property)
            if (property.sold) {
                binding.switchSold.isChecked = true
                binding.switchSold.isClickable = false
            }
        }
    }

    //Set chip checked if they are present in chipList
    private fun populateChips(property: Property) {
        binding.apply {
            chipParking.isChecked = true; chipCellar.isChecked = true; chipGarage.isChecked = true
            chipGarden.isChecked = true; chipView.isChecked = true; chipBalcony.isChecked = true
            chipPool.isChecked = true; chipElevator.isChecked = true; chipSchool.isChecked = true
            chipBar.isChecked = true;chipLocalCommerce.isChecked = true; chipPark.isChecked = true
            chipTransport.isChecked = true; chipCultural.isChecked = true
        }
        ViewHelper.checkSelectedType(property, binding.chipHouse)
        ViewHelper.checkSelectedType(property, binding.chipApartment)
        ViewHelper.checkSelectedType(property, binding.chipLand)
       ViewHelper.checkOnlySelectedChips(binding.chipGroupAsset, property.asset)
        ViewHelper.checkOnlySelectedChips(binding.chipGroupInterest, property.pointOfInterest)

        for (description in property.descriptionPhoto!!) {
            if (description.isNotEmpty()) {
                val chip = Chip(this)
                chip.text = description
                binding.chipGroup.addView(chip)
                chip.setOnClickListener { binding.chipGroup.removeView(chip) }
            } else {
                binding.chipGroup.visibility = View.GONE
            }
        }
    }
    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
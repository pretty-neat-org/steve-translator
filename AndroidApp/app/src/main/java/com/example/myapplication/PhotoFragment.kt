package com.example.myapplication


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

var language = "en"

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private val PICK_IMAGE = 1
    private val TAKE_PHOTO = 2
    private var currentPhotoPath: String = ""
    private  var currPhotoURI: Uri? = null


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_change_photo.setOnClickListener {
            val items = arrayOf("Pick photo", "Take photo")
            AlertDialog.Builder(context)
                .setTitle("Pick from:")
                .setItems(items) { dialog: DialogInterface?, which: Int ->
                    when (which) {
                        0 -> this.pickPhoto(PICK_IMAGE)
                        1 -> context?.let { context -> takePhoto(TAKE_PHOTO, context) }
                    }
                }
                .show()
        }
        btn_translate.setOnClickListener {
            if (currPhotoURI!= null) {
                upload()
            }

        }

        val spinner: Spinner = view.findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter.createFromResource(
                    requireActivity(),
                    R.array.languages_array,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    spinner.adapter = adapter
                }
        spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                language = parent.getItemAtPosition(pos) as String
                showToast(language + " selected")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

            fun showToast(message: String) {
                requireActivity().runOnUiThread {
                    Toast.makeText(  requireActivity(), message, Toast.LENGTH_LONG ).show()
                }
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun upload(){
        if (currPhotoURI != null) {
//            val myFile = File(currentPhotoPath as String)
//            val imgUri = Uri.fromFile(myFile)
            UploadUtility((activity as MainActivity?)!!).uploadFile(language, currPhotoURI!!) // Either Uri, File or String file path
        }
    }

    private fun pickPhoto(code: Int) {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            startActivityForResult(Intent.createChooser(this, getString(R.string.pick_image)), code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val imageUri = data.data
                if (imageUri != null) {
                    cropRequest(imageUri)
                }
            }
        } else if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (currentPhotoPath != null) {
                val myFile = File(currentPhotoPath as String)
                val imgUri = Uri.fromFile(myFile)
                cropRequest(imgUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun cropRequest(uri: Uri) {
        CropImage.activity(uri).setCropMenuCropButtonTitle("Select")
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .start(activity as MainActivity)
    }

    fun addPhoto(uri: Uri) {
        context?.let {
            Glide.with(it)
                .load(uri).circleCrop()
                .into(iv_photo)
            currPhotoURI = uri
        }
    }



    private fun takePhoto(code: Int, context: Context) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.example.myapplication.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, code)
                }
            }
        }
    }

    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


}

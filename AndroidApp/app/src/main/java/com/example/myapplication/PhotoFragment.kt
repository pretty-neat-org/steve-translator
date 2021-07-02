package com.example.myapplication


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
                var data: String

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
    fun upload() {
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
                .load(uri)
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

    fun draw(data: String){
        //data = "400 800 170 40 jedinstven";

        val words_list = data.split(",")


            Glide.with(this)
                .asBitmap()
                .load(currPhotoURI)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        val bm = resource.copy(Bitmap.Config.ARGB_8888, true)
                        val tf = Typeface.create("Helvetica", Typeface.BOLD)

                        for (word: String in words_list) {
                            val single_word_data = word.split(" ")

                            val scale_factor = 0.4

                            val x = single_word_data[0].toInt() / scale_factor
                            val y = single_word_data[1].toInt() / scale_factor
                            val width = single_word_data[2].toInt() / scale_factor
                            val height = single_word_data[3].toInt() / scale_factor
                            val text = single_word_data[4]

                            Log.d("info", text)

                            val paint = Paint()
                            paint.style = Paint.Style.FILL
                            paint.color = Color.BLACK
                            paint.typeface = tf
                            paint.textAlign = Paint.Align.LEFT
                            paint.textSize = (height).toFloat()

                            val textRect = Rect()
                            paint.getTextBounds(
                                text,
                                0,
                                text.length,
                                textRect
                            )

                            val canvas = Canvas(bm)

                            //If the text is bigger than the canvas , reduce the font size
                            if (textRect.width() >= canvas.width - 4)
                            //the padding on either sides is considered as 4, so as to appropriately fit in the text
                                paint.textSize -= 5

//                                //Calculate the positions
//                                val xPos = canvas.width.toFloat() / 2 + -2
//
//                                //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
//                                val yPos =
//                                    (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2) + 0

                            val mPaint = Paint()
                            mPaint.color = Color.WHITE
                            val mPath = Path()
                            val mRectF = RectF(
                                x.toFloat(),
                                y.toFloat(),
                                (x + width).toFloat(),
                                (y + height).toFloat()
                            )
                            mPath.addRect(mRectF, Path.Direction.CCW)
                            mPaint.setStrokeWidth(((height).toFloat()))
                            mPaint.style = Paint.Style.FILL
                            canvas.drawPath(mPath, mPaint)

                            canvas.drawText(text, x.toFloat(), (y+height).toFloat(), paint)

                        }
                        iv_photo.setImageBitmap(bm)
                    }

                })
       // }


    }


}

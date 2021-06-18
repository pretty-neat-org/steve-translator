package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.theartofdev.edmodo.cropper.CropImage
import java.io.IOException
import java.io.InputStream
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            val inputStream: InputStream
            if (resultCode == Activity.RESULT_OK) {
                try {
                    contentResolver.openInputStream(result.uri)?.let {
                        inputStream = it
                        result.uri?.let { uri ->
                            val fragment =
                                nav_host_fragment.childFragmentManager.fragments[0] as PhotoFragment

                            fragment.addPhoto(uri)

                            upload(uri)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, R.string.unable_to_open_image, Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun upload(uri: Uri){
        UploadUtility(this).uploadFile(uri) // Either Uri, File or String file path
    }
}
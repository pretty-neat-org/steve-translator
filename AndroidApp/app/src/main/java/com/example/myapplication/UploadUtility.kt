package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit


class UploadUtility(activity: Activity)  {

    var activity = activity;
    var dialog: ProgressDialog? = null
    var serverURL: String = "https://steve-translator-server.herokuapp.com"
    //var serverUploadDirectoryPath: String = "http://localhost:8080/uploads"
 //   var serverURL: String = "https://handyopinion.com/tutorials/UploadToServer.php"
//    var serverUploadDirectoryPath: String = "https://handyopinion.com/tutorials/uploads/"
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();


    fun uploadFile(language:String, sourceFilePath: String, uploadedFileName: String? = null) {
         uploadFile(language, File(sourceFilePath), uploadedFileName)

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun uploadFile(language:String, sourceFileUri: Uri, uploadedFileName: String? = null) {
        val pathFromUri = URIPathHelper().getPath(activity,sourceFileUri)
         uploadFile(language, File(pathFromUri), uploadedFileName)
    }

    fun uploadFile(language:String, sourceFile: File, uploadedFileName: String? = null)  {
        Thread {
            val mimeType = getMimeType(sourceFile);
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")

            }
            val fileName: String = if (uploadedFileName == null)  sourceFile.name else uploadedFileName
            toggleProgressDialog(true)
            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName,sourceFile.asRequestBody(mimeType!!.toMediaTypeOrNull()))
                        .build()

               // val request: Request = Request.Builder().url(serverURL+'-'+language).post(requestBody).build()
                val request: Request = Request.Builder().url(serverURL).post(requestBody).build()

                val response: Response = client.newCall(request).execute()
                val data: String = response.body!!.string()


//                val `in` = response.body
//                val inputStream: InputStream = `in`!!.byteStream()
//                Log.i("inputStream", "inputstream value = $inputStream")
//                // convert inputstram to bufferinoutstream
//                // convert inputstram to bufferinoutstream
//                val bufferedInputStream = BufferedInputStream(inputStream)
//                val bitmap = BitmapFactory.decodeStream(bufferedInputStream)
//                Log.i("bitmap", "bitmap value = $bitmap")


                if (response.isSuccessful) {

                    Log.d("File upload","success")
                    showToast(data)

                    val fragment=
                        ((activity as MainActivity?)!!).nav_host_fragment.childFragmentManager.fragments[0] as PhotoFragment
                    fragment.draw(data)


                } else {

                    Log.e("File upload", "failed")
                    Log.e("File upload", response.message)
                    showToast("File uploading failed")

                }
            } catch (ex: Exception) {

                ex.printStackTrace()
                Log.e("File upload", "Exception occured")
                showToast("Exception occured")

            }
            toggleProgressDialog(false)
        }.start()
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText( activity, message, Toast.LENGTH_LONG ).show()
        }
    }

    fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
            } else {
                dialog?.dismiss();
            }
        }
    }

}

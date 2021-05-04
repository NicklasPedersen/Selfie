package com.example.selfie

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SelfieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfie)
    }

    val REQUEST_IMAGE_CAPTURE = 1
    // save the uri
    var uri: Uri? = null

    /**
     * This is used to get result from another app, for example: image from camera
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // result from camera?
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageView = this.findViewById<ImageView>(R.id.custom_image)
            try {

                uri?.let {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        // This says that it's deprecated, but from what I can find, it
                        // will always work prior to API 28
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            uri
                        )
                        imageView.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun takePicture(takePictureButton: View) {
        // if we have a device that somehow installed the app without a camera, display an error
        // and disable the button
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            val txt = findViewById<TextView>(R.id.error_text)
            txt.text = getString(R.string.error_no_cam)
            txt.visibility = View.VISIBLE
            takePictureButton.isEnabled = false
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // save the uri so we know where to get the picture when the camera returns
        uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

}
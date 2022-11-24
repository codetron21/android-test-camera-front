package com.codetron.frontcamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imagePhoto: ImageView
    private lateinit var buttonCamera: Button

    private val launcherCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {

            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(EXTRA_PIC, File::class.java)
            } else {
                it.data?.getSerializableExtra(EXTRA_PIC) as File
            }

            val bitmap = rotateBitmap(BitmapFactory.decodeFile(myFile?.path))

            imagePhoto.setImageBitmap(bitmap)
        }
    }

    private val launcherCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagePhoto = findViewById(R.id.image_photo)
        buttonCamera = findViewById(R.id.button_camera)

        buttonCamera.setOnClickListener {
            if (!checkFrontCamera()) return@setOnClickListener

            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED).run {
                if (this) {
                    if (checkVersions()) {
                        runCamerax()
                    } else {

                    }
                } else {
                    launcherCameraPermission.launch(Manifest.permission.CAMERA)
                }
            }
        }
    }

    private fun runCamerax() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherCameraX.launch(intent)
    }

    private fun checkFrontCamera(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)

    private fun checkVersions() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    companion object {
        const val EXTRA_PIC = "EXTRA_PIC"
    }

}
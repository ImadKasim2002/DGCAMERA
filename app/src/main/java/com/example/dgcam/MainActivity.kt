package com.example.dgcam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    var camera: Camera?=null
    var preview: Preview?=null
    var imageCapture: ImageCapture?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
        {
           startCamera()
        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }



        findViewById<Button>(R.id.captureBtn).setOnClickListener {
            takePhoto()
        }


    }

    private fun takePhoto(){
        //savePhoto
        val photofile = File(externalMediaDirs.firstOrNull(), "camapp - ${System.currentTimeMillis()}.jpg")
        val output = ImageCapture.OutputFileOptions.Builder(photofile).build()
        imageCapture?.takePicture(output, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(applicationContext, "Image Saved", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
        {
           startCamera()
        }
        else
        {
            Toast.makeText(this, "Please accept permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCamera(){
        val myView: PreviewView = findViewById(R.id.cameraView)
        //startCamera
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            preview?.setSurfaceProvider(myView.createSurfaceProvider(camera?.cameraInfo))
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))

    }
}
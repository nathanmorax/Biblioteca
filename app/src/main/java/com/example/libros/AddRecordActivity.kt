package com.example.libros

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.nio.ByteBuffer


class AddRecordActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    private var actionBar: ActionBar? = null

    private lateinit var dbHelper: DatabaseHelper



    private lateinit var pTitleEt: EditText
    private lateinit var pAuthorEt: EditText
    private lateinit var pEditorialEt: EditText
    private lateinit var pYearEt: EditText
    private lateinit var pImageView: ImageView
    private lateinit var addRecordButton: Button

    private var book: ModelRecord? = null

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var cameraProvider: ProcessCameraProvider? = null
    private val imageCapture = ImageCapture.Builder().build()
    private var capturedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        actionBar = supportActionBar
        actionBar!!.title = "Add Information"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        pImageView = findViewById(R.id.bookImage)

        pImageView.setOnClickListener {

            imagePickDialog()
        }
        addRecordButton = findViewById(R.id.addButton)
        addRecordButton.setOnClickListener {
            inputDate()
        }

        pTitleEt = findViewById(R.id.bookTitle)
        pAuthorEt = findViewById(R.id.bookAuthor)
        pEditorialEt = findViewById(R.id.bookEditorial)
        pYearEt = findViewById(R.id.bookYear)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            var cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()
            cameraProvider?.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture)

        }, ContextCompat.getMainExecutor(this))

        // show book data
        book = intent.extras?.get("book") as? ModelRecord

        pTitleEt.setText(book?.title)
        pAuthorEt.setText(book?.author)
        pEditorialEt.setText(book?.editorial)
        pYearEt.setText(book?.year)

        capturedBitmap = book?.image?.toBitmap()
        if (capturedBitmap != null) {
            pImageView.setImageBitmap(capturedBitmap)
        } else {
            // TODO: image placeholder
        }
    }

    private fun inputDate() {
        val title = pTitleEt.text.toString().trim()
        val author = pAuthorEt.text.toString().trim()
        val editorial = pEditorialEt.text.toString().trim()
        val year = pYearEt.text.toString().trim()

        if (book != null) {
            book!!.title = title
            book!!.author = author
            book!!.editorial = editorial
            book!!.year = year
            book!!.image = capturedBitmap?.toBlob()

            dbHelper.updateBook(book!!)
            Toast.makeText(
                this,
                "Book saved successfully",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        } else {
            val id = dbHelper.insertBook(
                title,
                author,
                editorial,
                year,
                capturedBitmap
            )
            Toast.makeText(
                this,
                "Book added successfully ($id)",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }

    private fun imagePickDialog() {

        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Pick Image From:")
        builder.setItems(options) { dialog, which ->
            print("Pick image ($which)")

            if (which == 0) {
                if(!checkCameraPermission()){
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()){
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }

            }

        }
        builder.create().show()
    }

    private fun pickFromGallery() {
        val galleyIntent = Intent(Intent.ACTION_PICK)
        galleyIntent.type = "image/"

        startActivityForResult(
            galleyIntent,
            IMAGE_PICK_GALLERY_CODE
        )

    }

    private fun requestStoragePermission() {
        print("NEED STORAGE!")
        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), STORAGE_REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        print("Storage? $permission")
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val executor = ContextCompat.getMainExecutor(this)
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                super.onCaptureSuccess(imageProxy)
                // retrieve and resize image
                val rawBitmap = imageProxy.image?.toBitmap()
                capturedBitmap = rawBitmap?.let {
                    Bitmap.createScaledBitmap(it, it.width / 10, it.height / 10, false)
                }
                pImageView.setImageBitmap(capturedBitmap)
            }
            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                print("" + exception)
            }
        })
    }

    private fun requestCameraPermission() {
        print("NEED CAMERA!")
        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )
        val storage = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        print("Camera & Storage? $camera $storage")
        return camera == PackageManager.PERMISSION_GRANTED && storage == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isNotEmpty()) {

                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(
                            this,
                            "Camera and Storage Permission Required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
                STORAGE_REQUEST_CODE -> {
                    if(grantResults.isNotEmpty()) {

                        val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                        if (storageAccepted) {
                            pickFromGallery()
                        } else {
                            Toast.makeText(
                                this,
                                "Storage Permission Required",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity((data!!.data))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }

            else if(requestCode == IMAGE_PICK_CAMERA_CODE) {
//                CropImage.activity((imageUri))
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this)
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
//                val result = CropImage.getActivityResult(data)
//                if (resultCode == Activity.RESULT_OK ) {
//                    val resultUri = result.uri
//                    imageUri = resultUri
//                    pImageView.setImageURI(resultUri)
//                }
//                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    var error = result.error
//
//                    Toast.makeText(
//                        this,
//                        ""+error,
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
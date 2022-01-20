package com.sampam.captureandsend

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sampam.captureandsend.databinding.ActivityMainBinding
import java.io.OutputStream
import java.lang.Exception

lateinit var binding:ActivityMainBinding
lateinit var photo:Bitmap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
        binding.button.setOnClickListener {
            val intent = Intent().apply {
                action = MediaStore.ACTION_IMAGE_CAPTURE
            }
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(intent, 201)
            }
            binding.button2.setOnClickListener{
                val shareintent=Intent(Intent.ACTION_SEND)
                shareintent.type="image/jpeg"
                val values=ContentValues()
                values.put(MediaStore.Images.Media.TITLE,"captured image")
                values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
                val uri:Uri?=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                var outputStream:OutputStream?=null
                try {
                    if(uri!=null){
                        outputStream=contentResolver.openOutputStream(uri)
                        photo.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                        outputStream?.close()
                    }
                }catch (e:Exception){
                    Log.d("MainActivity", "exception hai bhai")
                }
                shareintent.putExtra(Intent.EXTRA_STREAM,uri)
                startActivity(shareintent)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==201 && resultCode==Activity.RESULT_OK){
            photo =data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(photo)
        }
    }
}
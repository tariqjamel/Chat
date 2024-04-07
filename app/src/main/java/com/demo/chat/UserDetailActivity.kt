package com.demo.chat

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.demo.chat.databinding.ActivityUserDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDetailActivity : AppCompatActivity() {

    lateinit var btnSaveUserDetail: Button

    lateinit var binding: ActivityUserDetailBinding
    private val taskDesc = Firebase.firestore.collection("Chat")
    lateinit var upload_Image: ImageView
    private var imageUri: Uri? = null
    lateinit var imageUrl: String
    private lateinit var progressDialog: ProgressDialog
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnSaveUserDetail = findViewById(R.id.btnSaveUserDetail)

         upload_Image = findViewById(R.id.upload_image)
        upload_Image.setOnClickListener{
            selectImage()
        }

        val etName = findViewById<EditText>(R.id.etName)
        val etBio = findViewById<EditText>(R.id.userBio)
        btnSaveUserDetail.setOnClickListener {
            val userName = etName.text.toString()
            val userBio = etBio.text.toString()

            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()

                val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("Username", userName)
                editor.putString("userBio",userBio)
                editor.putString("UserImage", imageUrl)
                editor.apply()

                Toast.makeText(this, "Welcome $userName", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && data != null && data.data != null) {
            imageUri = data.data
            binding.uploadImage.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading File....")
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        imageUrl = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("images/$imageUrl")

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                binding.uploadImage.setImageURI(null)
                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this, "Failed to Upload", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failed to Upload: ${e.message}", e)

            }
    }

}
package com.demo.chat

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.demo.chat.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private val userDescription = Firebase.firestore.collection("NewTask")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var firebaseAuth = FirebaseAuth.getInstance()
        var editImg = findViewById<ImageView>(R.id.edit_img)
        editImg.setOnClickListener{
            val intent = Intent(this, UserDetailActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userProfile = sharedPreferences.getString("UserImage", null)
        val username = sharedPreferences.getString("Username", null)
        val userBio = sharedPreferences.getString("userBio",null)

        val etUserBio = findViewById<TextView>(R.id.user_Bio)
        etUserBio.text = userBio
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        tvUserName.text = username

        val storageReference = FirebaseStorage.getInstance().reference.child("images/$userProfile")

        val localFile = File.createTempFile("tempImage","png")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.updateImage.setImageBitmap(bitMap)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("ImagaActivity", "Error fetching image", exception)
        }

        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.itemAccount -> { val intent = Intent(this, UserDetailActivity::class.java)
                startActivity(intent) }
            R.id.itemSecurity -> Toast.makeText(this, "Highly Secure", Toast.LENGTH_SHORT).show()
            R.id.itemPrivacy -> { val intent = Intent(this, AccountPrivacyActivity::class.java)
                startActivity(intent) }
            R.id.itemHelp -> Toast.makeText(this, "Please wait for help.", Toast.LENGTH_SHORT).show()
            R.id.itemAbout -> { val intent = Intent(this, AccountAboutActivity::class.java)
                startActivity(intent) }
            R.id.itemLogOut ->   { FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)  }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
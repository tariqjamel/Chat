package com.demo.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.chat.databinding.ActivityMainBinding
import com.demo.chat.databinding.ActivityUserBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MainActivity : AppCompatActivity(), ContactAdapter.ItemClickListener {

    private  lateinit var binding : ActivityMainBinding
    lateinit var recyclerView : RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    var contactList = arrayListOf<ContactDetail>()

    lateinit var contactName : String
    lateinit var contactNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fibContact = findViewById<FloatingActionButton>(R.id.fibContact)
        recyclerView = findViewById(R.id.recyclerView)

        val user_detail = findViewById<ImageView>(R.id.user_detail)

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userProfile = sharedPreferences.getString("UserImage", null)

        val storageReference = FirebaseStorage.getInstance().reference.child("images/$userProfile")

        val localFile = File.createTempFile("tempImage","png")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.userDetail.setImageBitmap(bitMap)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("ImagaActivity", "Error fetching image", exception)
        }

        fibContact.setOnClickListener{
            val intent = Intent(this,ContactActivity::class.java)
            startActivity(intent)
            finish()
        }

        user_detail.setOnClickListener{
            val intent = Intent(this,UserActivity::class.java)
            startActivity(intent)
            finish()
        }

   /*     contactName = intent.getStringExtra("name").toString()
        contactNumber = intent.getStringExtra("Number").toString()
*/
        contactName = "Friend Group"

        val obj = ContactDetail()
        obj.name = contactName
        obj.imageResource = R.drawable.grpdpp

        contactList.add(obj)

        contactAdapter = ContactAdapter(contactList,this,this)
        recyclerView.adapter = contactAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

      /*     val filter = IntentFilter("NEW_MESSAGE_SENT")
           registerReceiver(messageSentReceiver, filter)*/
    }

    /*private val messageSentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "NEW_MESSAGE_SENT") {
                val contactName = intent.getStringExtra("Contact name")
                val contactNumber = intent.getStringExtra("Contact Number")

                val newContact = ContactDetail()
                newContact.name = contactName
                newContact.number = contactNumber

                contactList.add(newContact)
                contactAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(messageSentReceiver)
        super.onDestroy()
    }*/

    override fun onItemClick(position: Int) {
        val clickContact = contactList[position]
        val intent = Intent(this@MainActivity, ChatActivity::class.java)
        intent.putExtra("Contact name", clickContact.name)
        intent.putExtra("Contact Number", clickContact.number)
        intent.putExtra("Contact IMAGE", clickContact.imageResource)
        startActivity(intent)
    }

}
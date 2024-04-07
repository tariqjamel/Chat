package com.demo.chat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import androidx.appcompat.widget.SearchView

class ContactActivity : AppCompatActivity(), ContactAdapter.ItemClickListener {

    lateinit var rvContactList: RecyclerView
    var contactList = arrayListOf<ContactDetail>()
   // lateinit var searchView : SearchView
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

      //  searchView = findViewById(R.id.search_View)
        rvContactList = findViewById(R.id.rvContactList)
        rvContactList.layoutManager = LinearLayoutManager(this)

        val permission = Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadContact()
        } else {
            requestPermissionLauncher.launch(permission)
        }

      /*  searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Update the filtered list as the user types in the search view.
                contactAdapter.filter(newText.orEmpty())
                return true
            }
        })*/
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            loadContact()
        } else {
            Toast.makeText(this, "Grant Permission in Setting", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    fun loadContact() {
        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        while (contacts?.moveToNext() == true) {
            val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val imageResource = fetchImageResourceForContact(name)

            val obj = ContactDetail()
            obj.name = name
            obj.number = number
            obj.imageResource = imageResource

            contactList.add(obj)
        }
        contacts?.close()

        fun MutableList<ContactDetail>.sortAlphabetically() {
            this.sortBy { it.name }
        }
        contactList.sortAlphabetically()

        contactAdapter = ContactAdapter(contactList, this, this)
        rvContactList.adapter = contactAdapter

        Log.d("Data", "Original List Size: ${contactList.size}")

    }

    override fun onItemClick(position: Int) {
        val clickContact = contactList[position]
        val intent = Intent(this@ContactActivity, PersonalChatActivity::class.java)
        intent.putExtra("Contact name", clickContact.name)
        intent.putExtra("Contact Number", clickContact.number)
        intent.putExtra("Contact IMAGE", clickContact.imageResource)
        startActivity(intent)
    }

    private fun fetchImageResourceForContact(contactName: String): Int {
        val imageMap = mapOf(
            "Contact1" to R.drawable.ic_emoji,
        )
        return imageMap[contactName] ?: R.drawable.ic_emoji
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package com.demo.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//const val TOPIC = "/topics/myTopic2"

class PersonalChatActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("chat_pessages_personal")
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    lateinit var chatRecyclerView : RecyclerView
    lateinit var contactName: String

    val TAG = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_chat)

        contactName = intent.getStringExtra("Contact name").toString()
        val tvcontactName: TextView = findViewById(R.id.tvNamePersonal)
        tvcontactName.text = contactName
        val contactImage = intent.getIntExtra("Contact IMAGE", 0)
        val contactImageView: ImageView = findViewById(R.id.IVContactActivity)
        contactImageView.setImageResource(contactImage)

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        chatRecyclerView = findViewById(R.id.rvMessagePersonal)
        adapter = ChatAdapter(messages,userId!!)
        // chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager

        chatRecyclerView.adapter = adapter

        val sendButton = findViewById<ImageView>(R.id.sendMessagePersonal)
        val messageEditText = findViewById<EditText>(R.id.etMessagePersonal)

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("Username", null)

        //  val user = FirebaseAuth.getInstance().currentUser
        val currentUserId = user.uid

        /*     FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
             FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                 FirebaseService.token = it.token
                 etToken.setText(it.token)
             }*/


        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = Message(messageText, username!!, System.currentTimeMillis(), currentUserId!!)
                sendMessageToFirebase(message)
                messageEditText.text.clear()
            }

            // FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            //    val recipientToken = etToken.text.toString()
            //    if(userId != currentUserId) {
            if (messageText.isNotEmpty()) {
                PushNotification(
                    Message(messageText, username!!),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
                //     }
            }
        }

        messagesCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            messages.clear()

            snapshot?.documents?.forEach { document ->
                val message = document.toObject(Message::class.java)
                message?.let { messages.add(it) }
            }

            messages.sortWith(Comparator { lhs, rhs -> rhs.timestamp.compareTo(lhs.timestamp) })

            adapter.notifyDataSetChanged()
        }
    }

    private fun sendMessageToFirebase(message: Message) {
        messagesCollection.add(message)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}
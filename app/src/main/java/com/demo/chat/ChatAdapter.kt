package com.demo.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class ChatAdapter(private val messages: MutableList<Message>, val currentUserId : String)
    : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val SENT_MESSAGE = 1
    private val RECEIVED_MESSAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == SENT_MESSAGE) {
            val view = inflater.inflate(R.layout.sent_message_item, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.received_message_item, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bindSentMessage(message.text, message.timestamp, message.username)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bindReceivedMessage(message.text, message.timestamp, message.username)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (messages[position].userId == currentUserId) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    open inner class ChatViewHolder(v: View) : RecyclerView.ViewHolder(v)

    inner class SentMessageViewHolder(itemView: View) : ChatViewHolder(itemView) {
        private val sentMessageText: TextView = itemView.findViewById(R.id.sentMessageText)
        private val sentTimeText: TextView = itemView.findViewById(R.id.sentTime)
        private val user_Name: TextView = itemView.findViewById(R.id.user_Name)

        fun bindSentMessage(message: String, timestamp: Long,userName: String) {
            sentMessageText.text = message
            val formattedTime = formatTimestamp(timestamp)
            sentTimeText.text = formattedTime
            user_Name.text = userName
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : ChatViewHolder(itemView) {
        private val receivedMessageText: TextView = itemView.findViewById(R.id.receivedMessageText)
        private val receivedTimeText: TextView = itemView.findViewById(R.id.receivedTime)
        private val user_Name: TextView = itemView.findViewById(R.id.sender_Name)

        fun bindReceivedMessage(message: String, timestamp: Long,userName: String) {
            receivedMessageText.text = message
            val formattedTime = formatTimestamp(timestamp)
            receivedTimeText.text = formattedTime
            user_Name.text = userName
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return sdf.format(calendar.time)
    }
}
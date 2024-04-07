package com.demo.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView

class ContactAdapter(val list: List<ContactDetail>, val context: Context, val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var originalList: List<ContactDetail> = list
    private var filteredList: List<ContactDetail> = originalList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = list[position]
        holder.name.text = listItem.name
        holder.number.text = listItem.number
        holder.imageView.setImageResource(listItem.imageResource)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView = v.findViewById(R.id.tv_name)
        var number: TextView = v.findViewById(R.id.tv_number)
        var imageView: ImageView = itemView.findViewById(R.id.image_View)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }
    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun filter(query: String) {
        filteredList = if(query.isEmpty()) {
            originalList
        } else {
            originalList.filter { contact ->
                val containsName = contact.name?.contains(query, ignoreCase = true) ?: false
                val containsNumber = contact.number?.contains(query) ?: false
                containsName || containsNumber
            }
        }
        notifyDataSetChanged()
        Log.d("Filter", "Filtered List Size: ${filteredList.size}")

    }

}
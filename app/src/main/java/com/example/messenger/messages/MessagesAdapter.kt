package com.example.messenger.messages

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.data.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesAdapter(private val currentUId: String)
    : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    var messages = listOf<Message>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.message)
        val timeSent: TextView = view.findViewById(R.id.time_sent)
    }

    fun setData(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.holder_message,
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.message.text = message.text

        setMessageGravity(holder, message.from)
        formatDate(holder, message.timestamp)
    }

    private fun formatDate(holder: ViewHolder, timestamp: Long) {
        val dateFormat = "dd-MM-yyyy HH:mm:ss"
        val date = Date(timestamp)
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        holder.timeSent.text = sdf.format(date)
    }

    private fun setMessageGravity(holder: ViewHolder, from: String ) {
        val messageLayoutParams = holder.message.layoutParams as LinearLayout.LayoutParams
        val timeLayoutParams = holder.timeSent.layoutParams as LinearLayout.LayoutParams
        if (from == currentUId) {
            messageLayoutParams.gravity = Gravity.END
            timeLayoutParams.gravity = Gravity.END
        } else {
            messageLayoutParams.gravity = Gravity.START
            timeLayoutParams.gravity = Gravity.START
        }
        holder.message.layoutParams = messageLayoutParams
        holder.timeSent.layoutParams = timeLayoutParams
    }

    override fun getItemCount() = messages.size
}
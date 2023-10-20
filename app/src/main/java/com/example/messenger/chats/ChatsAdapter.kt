package com.example.messenger.chats

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.data.User
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter constructor(private val viewModel: ChatsViewModel,
                               private val context: Context,
                               private val messagesDisplay: MessageDisplayListener):
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private var chats = mutableListOf<User>()
    private var images = listOf<Uri>()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_chat, parent, false))
    }

    fun setChatsList(chatsList: MutableList<User>) {
        this.chats = chatsList
    }

    fun setImages(images: List<Uri>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = chats[position]
        holder.name.text = friend.fullName
        loadImage(images[position], holder)

        holder.view.setOnClickListener {
            messagesDisplay.onDisplayMessages(friend)
        }

        viewModel.listenForNewMessage(friend.userId, holder)

    }

    private fun loadImage(uri: Uri, holder: ViewHolder) {
        Glide.with(context)
            .load(uri)
            .into(holder.mainPhoto)
    }
    override fun getItemCount() = chats.size

    interface MessageDisplayListener {
        fun onDisplayMessages(friend: User)
    }
}
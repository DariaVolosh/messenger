package com.example.messenger.presenter.chats

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.data.Message
import com.example.messenger.data.User
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter constructor(
    private val messagesDisplay: MessageDisplayListener,
    private val loadImage: LoadImage
):
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private var chats = listOf<User>()
    private var images = listOf<Uri>()
    private var lastMessages = listOf<Message>()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_chat, parent, false))
    }

    fun setChatsList(chatsList: List<User>) {
        this.chats = chatsList
    }

    fun setImages(images: List<Uri>) {
        this.images = images
    }

    fun setLastMessages(lastMessages: List<Message>) {
        this.lastMessages = lastMessages
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < images.size && position < lastMessages.size && position < chats.size) {
            val friend = chats[position]
            holder.name.text = friend.fullName
            loadImage.loadImage(images[position], holder.mainPhoto)


            holder.view.setOnClickListener {
                messagesDisplay.onDisplayMessages(friend)
            }

            holder.message.text = lastMessages[position].text
        }
    }

    override fun getItemCount() = chats.size

    interface MessageDisplayListener {
        fun onDisplayMessages(friend: User)
    }

    interface LoadImage {
        fun loadImage(uri: Uri, imageView: ImageView)
    }
}
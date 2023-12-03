package com.example.messenger.presenter.chats

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatsAdapter(
    private val loadImage: (Uri, ImageView) -> Unit,
    private val displayChat: (String) -> Unit
): RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private var chats = listOf<User>()
    private var images = listOf<Uri>()
    private var lastMessages = listOf<Message>()
    private var onlineStatuses = listOf<Flow<Boolean>>()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
        val online: TextView = view.findViewById(R.id.online)
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
    }

    fun setOnlineStatus(online: List<Flow<Boolean>>) {
        this.onlineStatuses = online
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < images.size && position < lastMessages.size && position < chats.size) {
            val friend = chats[position]
            val online = onlineStatuses[position]
            holder.name.text = friend.fullName
            holder.message.text = lastMessages[position].text
            CoroutineScope(Dispatchers.IO).launch {
                online.collect {online ->
                    withContext(Dispatchers.Main) {
                        holder.online.text =
                            if (online) {
                                holder.online.setTextColor(holder.view.context.getColor(R.color.green))
                                holder.view.context.getString(R.string.active_now)
                            } else {
                                holder.online.setTextColor(holder.view.context.getColor(R.color.red))
                                holder.view.context.getString(R.string.offline)
                            }
                    }
                }
            }

            loadImage(images[position], holder.mainPhoto)

            holder.view.setOnClickListener {
                displayChat(friend.userId)
            }

        }
    }

    override fun getItemCount() = chats.size
}
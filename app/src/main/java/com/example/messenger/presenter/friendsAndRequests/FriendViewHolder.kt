package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.messenger.R
import de.hdodenhof.circleimageview.CircleImageView

class FriendViewHolder(
    itemView: View,
    private val loadImage: (Uri, ImageView) -> Unit,
    private val openChat: (String) -> Unit
):
    DataAdapterViewHolder(itemView) {

    fun bind(item: DataModel.Friend, photoUri: Uri) {
        val mainPhoto: CircleImageView = itemView.findViewById(R.id.main_photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val login: TextView = itemView.findViewById(R.id.login)
        val button: TextView = itemView.findViewById(R.id.send_message_button)

        name.text = item.name
        login.text = item.login
        loadImage(photoUri, mainPhoto)

        button.setOnClickListener {
            openChat(item.userId)
        }
    }
}
package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.messenger.R
import com.example.messenger.presenter.messages.MessagesFragment
import de.hdodenhof.circleimageview.CircleImageView

class FriendViewHolder(itemView: View, private val viewModel: FriendsViewModel):
    DataAdapterViewHolder(itemView) {

    fun bind(item: DataModel.Friend, photoUri: Uri) {
        val mainPhoto: CircleImageView = itemView.findViewById(R.id.main_photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val login: TextView = itemView.findViewById(R.id.login)
        val button: TextView = itemView.findViewById(R.id.send_message_button)

        name.text = item.name
        login.text = item.login
        viewModel.loadImage(photoUri, mainPhoto)
        button.setOnClickListener {openChat(item.userId)}
    }

    private fun openChat(uId: String) {
        val bundle = bundleOf()
        bundle.putString(MessagesFragment.FRIEND_UID, uId)
        viewModel.getUserObjectById(uId)
        itemView.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            viewModel.friend.observe(lifecycleOwner) {
                viewModel.openChat()
            }
        }
    }
}
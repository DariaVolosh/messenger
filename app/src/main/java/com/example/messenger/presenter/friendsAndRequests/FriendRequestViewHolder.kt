package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.example.messenger.R
import de.hdodenhof.circleimageview.CircleImageView

class FriendRequestViewHolder(itemView: View, private val viewModel: FriendsViewModel):
    DataAdapterViewHolder(itemView) {

    fun bind(item: DataModel.FriendRequest, photoUri: Uri) {
        val mainPhoto: CircleImageView = itemView.findViewById(R.id.main_photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val login: TextView = itemView.findViewById(R.id.login)
        val button: ImageButton = itemView.findViewById(R.id.add_to_friends_button)

        name.text = item.name
        login.text = item.login
        uploadImage(photoUri, mainPhoto)
        button.setOnClickListener {addFriend(item.userId)}
    }

    private fun addFriend(clickedUserId: String) {
        itemView.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            viewModel.currentUser.observe(lifecycleOwner) { currentUser ->
                currentUser.receivedFriendRequests.remove(clickedUserId)
                currentUser.friends += clickedUserId
                viewModel.updateUser(currentUser)
                viewModel.getUserObjectById(clickedUserId)
            }
        }

        itemView.findViewTreeLifecycleOwner()?.let {
            viewModel.friend.observe(it){ friend ->
                val currentUser = viewModel.currentUser.value
                currentUser?.let {user ->
                    friend.friends += user.userId
                    viewModel.updateUser(friend)
                    val friends = user.friends
                    val requests = user.receivedFriendRequests
                    val friendsAndRequests = friends + requests
                    viewModel.getUsersFromUId(friendsAndRequests)
                }
            }
        }
    }

    private fun uploadImage(uri: Uri, mainPhoto: CircleImageView) {
        Glide.with(itemView.context)
            .load(uri)
            .into(mainPhoto)
    }
}
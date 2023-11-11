package com.example.messenger.friendsAndRequests

import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.messages.MessagesFragment
import de.hdodenhof.circleimageview.CircleImageView
class DataAdapterViewHolder(
    itemView: View,
    private val viewModel: FriendsViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val navController: NavController
):
    RecyclerView.ViewHolder(itemView) {

    private fun addFriend(clickedUserId: String) {
        val currentUser = viewModel.currentUser
        currentUser.observe(lifecycleOwner) {currentUser ->
            currentUser.receivedFriendRequests.remove(clickedUserId)
            currentUser.friends += clickedUserId
            viewModel.updateUser(currentUser)
            viewModel.getUserObjectById(clickedUserId)
        }

        viewModel.friend.observe(lifecycleOwner){friend ->
            val currentUser = currentUser.value!!
            friend.friends += currentUser.userId
            viewModel.updateUser(friend)

            val friends = currentUser.friends
            val requests = currentUser.receivedFriendRequests
            val friendsAndRequests = friends + requests
            viewModel.getUsersFromUId(friendsAndRequests)
        }
    }

    private fun bindFriendRequest(item: DataModel.FriendRequest, photoUri: Uri) {
        val mainPhoto: CircleImageView = itemView.findViewById(R.id.main_photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val login: TextView = itemView.findViewById(R.id.login)
        val button: ImageButton = itemView.findViewById(R.id.add_to_friends_button)

        name.text = item.name
        login.text = item.login
        uploadImage(photoUri, mainPhoto)
        button.setOnClickListener {addFriend(item.userId)}
    }

    private fun bindFriend(item: DataModel.Friend, photoUri: Uri) {
        val mainPhoto: CircleImageView = itemView.findViewById(R.id.main_photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val login: TextView = itemView.findViewById(R.id.login)
        val button: TextView = itemView.findViewById(R.id.send_message_button)

        name.text = item.name
        login.text = item.login
        uploadImage(photoUri, mainPhoto)
        button.setOnClickListener {openChat(item.userId)}
    }

    private fun openChat(uId: String) {
        val bundle = bundleOf()
        bundle.putString(MessagesFragment.FRIEND_UID, uId)
        navController.navigate(R.id.messages_fragment, bundle)
    }

    private fun uploadImage(uri: Uri, mainPhoto: CircleImageView) {
        Glide.with(itemView.context)
            .load(uri)
            .into(mainPhoto)
    }

    private fun bindText(item: DataModel.FriendsRequestsText) {
        val text: TextView = itemView.findViewById(R.id.friend_requests_text)
        text.text = item.text
    }

    fun bind(dataModel: DataModel, photoUri: Uri?) {
        when (dataModel) {
            is DataModel.FriendRequest -> bindFriendRequest(dataModel, photoUri!!)
            is DataModel.Friend -> bindFriend(dataModel, photoUri!!)
            is DataModel.FriendsRequestsText -> bindText(dataModel)
        }
    }
}
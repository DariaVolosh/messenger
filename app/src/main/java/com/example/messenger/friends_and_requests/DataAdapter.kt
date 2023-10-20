package com.example.messenger.friends_and_requests

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import java.lang.IllegalArgumentException
import javax.inject.Inject

class DataAdapter @Inject constructor (
    val viewModel: FriendsViewModel,
    val lifecycleOwner: LifecycleOwner,
    val navController: NavController
): RecyclerView.Adapter<DataAdapterViewHolder>(){

    companion object {
        private const val TYPE_FRIEND = 0
        private const val TYPE_FRIEND_REQUEST = 1
        private const val TYPE_FRIEND_REQUESTS_TEXT = 2
    }

    private var friendsAndRequestsList = listOf<DataModel>()
    private var images = listOf<Uri>()

    fun setFriendsAndRequestsList(friendsAndRequestsList: List<DataModel>) {
        this.friendsAndRequestsList = friendsAndRequestsList
    }

    fun setImages(images: List<Uri>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapterViewHolder {
        val layout = when (viewType) {
            TYPE_FRIEND -> R.layout.holder_friend
            TYPE_FRIEND_REQUEST -> R.layout.holder_friend_request
            TYPE_FRIEND_REQUESTS_TEXT -> R.layout.friends_requests_text
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return DataAdapterViewHolder(view, viewModel, lifecycleOwner, navController)
    }


    override fun onBindViewHolder(holder: DataAdapterViewHolder, position: Int) {
        val element = friendsAndRequestsList[position]
        if (element !is DataModel.FriendsRequestsText) {
            val indexOfImage = images.indexOfFirst {
                it.toString().contains(
                    if (element is DataModel.FriendRequest) {
                        element.userId
                    } else {
                        (element as DataModel.Friend).userId
                    }
                )
            }
            holder.bind(friendsAndRequestsList[position], images[indexOfImage])
        } else {
            holder.bind(friendsAndRequestsList[position], null)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (friendsAndRequestsList[position]) {
            is DataModel.Friend -> TYPE_FRIEND
            is DataModel.FriendRequest -> TYPE_FRIEND_REQUEST
            is DataModel.FriendsRequestsText -> TYPE_FRIEND_REQUESTS_TEXT
        }
    }

    override fun getItemCount() = friendsAndRequestsList.size
}
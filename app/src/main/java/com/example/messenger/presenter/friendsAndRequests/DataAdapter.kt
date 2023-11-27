package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import javax.inject.Inject

class DataAdapter @Inject constructor (
    val viewModel: FriendsViewModel
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
        val viewHolder = when (viewType) {
            TYPE_FRIEND -> FriendViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.holder_friend, parent, false),
                viewModel)
            TYPE_FRIEND_REQUEST -> FriendRequestViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.holder_friend_request, parent, false),
                viewModel)
            TYPE_FRIEND_REQUESTS_TEXT -> FriendRequestsTextViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.friends_requests_text, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid type")
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: DataAdapterViewHolder, position: Int) {
        val element = friendsAndRequestsList[position]
        if (element !is DataModel.FriendsRequestsText) {
            if (element is DataModel.Friend) {
                val indexOfImage = images.indexOfFirst {uri ->
                    uri.toString().contains(element.userId)
                }

                (holder as FriendViewHolder).bind(
                    friendsAndRequestsList[position] as DataModel.Friend,
                    images[indexOfImage]
                )
            } else {
                val indexOfImage = images.indexOfFirst {uri ->
                    uri.toString().contains((element as DataModel.FriendRequest).userId)
                }

                (holder as FriendRequestViewHolder).bind(
                    friendsAndRequestsList[position] as DataModel.FriendRequest,
                    images[indexOfImage]
                )
            }
        } else {
            (holder as FriendRequestsTextViewHolder).bind(
                friendsAndRequestsList[position] as DataModel.FriendsRequestsText
            )
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
package com.example.messenger.presenter.friendsAndRequests

import android.view.View
import android.widget.TextView
import com.example.messenger.R

class FriendRequestsTextViewHolder(itemView: View):
    DataAdapterViewHolder(itemView) {
    private val requestsText: TextView = itemView.findViewById(R.id.friend_requests_text)

    fun bind(friendRequestsText: DataModel.FriendsRequestsText) {
        requestsText.text = friendRequestsText.text
    }
}
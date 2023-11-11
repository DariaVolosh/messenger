package com.example.messenger.friendsAndRequests

sealed class DataModel {
    data class FriendRequest(
        val name: String,
        val login: String,
        val userId: String
    ): DataModel()

    data class Friend(
        val name: String,
        val login: String,
        val userId: String
    ): DataModel()

    data class FriendsRequestsText(
        val text: String
    ): DataModel()
}
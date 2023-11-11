package com.example.messenger.data
data class User(
    var fullName: String,
    var email: String,
    val login: String,
    var userId: String,
    var friends: MutableList<String>,
    var receivedFriendRequests: MutableList<String>,
    var chats: MutableList<String>) {
    constructor() :
            this("", "", "", "",
                mutableListOf(), mutableListOf(), mutableListOf()
            )
}
package com.example.messenger.friends_and_requests

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.data.User
import javax.inject.Inject

class FriendsViewModel @Inject constructor(private val model: Model): ViewModel() {
    val friendsAndRequestsList = MutableLiveData<MutableList<User>>()
    val images = MutableLiveData<List<Uri>>()
    val friendRequests = MutableLiveData<MutableList<String>>()
    val friend = MutableLiveData<User>()

    val currentUser = model.currentUserObject

    init {
        listenForNewFriendRequests()
    }

    fun getUsersFromUId(list: List<String>) {
        model.getUsersFromUId(list, friendsAndRequestsList)
    }

   fun downloadImages(list: List<User>) {
       model.downloadImages(list, images)
   }

    fun updateUser(user: User) {
        model.updateUser(user)
    }

    fun getUserObjectById(id: String) {
        model.getUserObjectById(id, friend)
    }

    private fun listenForNewFriendRequests() {
        model.listenForNewFriendRequests(friendRequests)
    }
}
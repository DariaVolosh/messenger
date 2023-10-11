package com.example.messenger.friends

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.MyApp
import com.example.messenger.data.User

class FriendsViewModel(val app: MyApp): ViewModel() {
    private val model = Model(app)
    val friendsList = MutableLiveData<MutableList<User>>()
    val requestsList = MutableLiveData<MutableList<User>>()
    val friendsImages = MutableLiveData<List<Uri>>()
    val requestsImages = MutableLiveData<List<Uri>>()
    val friend = MutableLiveData<User>()

    val currentUser = model.currentUserObject
    val friendRequests = MutableLiveData<MutableList<String>>()

    init {
        model.listenForNewFriendRequests(friendRequests)
    }

    fun getUsersFromUId(list: List<String>, friends: Boolean) {
        if (friends) model.getUsersFromUId(list, friendsList)
        else model.getUsersFromUId(list, requestsList)
    }

    fun downloadImages(list: List<User>, friends: Boolean) {
        if (friends) model.downloadImages(list, friendsImages)
        else model.downloadImages(list, requestsImages)
    }

    fun updateUser(user: User) {
        model.updateUser(user)
    }

    fun getUserObjectById(id: String) {
        model.getUserObjectById(id, friend)
    }
}
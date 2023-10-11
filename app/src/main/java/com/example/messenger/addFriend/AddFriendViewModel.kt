package com.example.messenger.addFriend

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.MyApp
import com.example.messenger.data.User

class AddFriendViewModel(val app: MyApp) : ViewModel() {
    private val model = Model(app)
    val foundUsers = MutableLiveData<List<User>>()
    var previousQuery: String = ""
    val images = MutableLiveData<List<Uri>>()

    fun searchUserByLogin(loginQuery: String) {
        previousQuery = loginQuery
        model.searchUserByLogin(loginQuery, foundUsers)
    }

    fun getCurrentUserId() = model.getCurrentUserUId()

    fun downloadImages(list: List<User>) {
        model.downloadImages(list, images)
    }

    fun updateUser(updatedUser: User) {
        model.updateUser(updatedUser)
    }
}
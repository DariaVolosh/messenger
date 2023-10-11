package com.example.messenger.chats

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.MyApp
import com.example.messenger.data.User

class ChatsViewModel(val app: MyApp) : ViewModel() {
    private val model = Model(app)
    val mainPhotoUri = MutableLiveData<Uri>()
    val chatList = MutableLiveData<MutableList<User>>()
    val photoUris = MutableLiveData<List<Uri>>()

    init {
        listenForNewChat()
    }

    private fun listenForNewChat() { model.listenForNewChat(chatList) }

    fun downloadImage() { model.downloadMyMainPhoto(mainPhotoUri) }

    fun downloadImages(list: List<User>) { model.downloadImages(list, photoUris)}

    fun listenForNewMessage(friendId: String, holder: ChatsAdapter.ViewHolder) {
        model.listenForNewMessage(friendId, holder)
    }
}
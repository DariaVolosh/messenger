package com.example.messenger.chats

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.MyApp
import com.example.messenger.data.User

class ChatsViewModel(val app: MyApp) : ViewModel() {
    private val model = Model(app)
    val mainPhotoUri = MutableLiveData<Uri>()
    val mainPhotoBitmap = MutableLiveData<Bitmap>()
    val chatList = MutableLiveData<MutableList<User>>()
    val photoUris = MutableLiveData<List<Uri>>()

    init {
        listenForNewChat()
    }

    private fun listenForNewChat() { model.listenForNewChat(chatList) }

    fun downloadImage(lifecycleOwner: LifecycleOwner) {
        model.downloadMyMainPhoto(mainPhotoUri, mainPhotoBitmap, lifecycleOwner)
    }

    fun downloadImages(list: List<User>) { model.downloadImages(list, photoUris)}

    fun listenForNewMessage(friendId: String, holder: ChatsAdapter.ViewHolder) {
        model.listenForNewMessage(friendId, holder)
    }
}
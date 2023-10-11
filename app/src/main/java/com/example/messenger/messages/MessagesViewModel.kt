package com.example.messenger.messages

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.MyApp
import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.google.firebase.database.DatabaseReference

class MessagesViewModel(val app: MyApp): ViewModel() {
    private val model = Model(app)

    val friendObject = MutableLiveData<User>()
    val friendPhotoUri = MutableLiveData<Uri>()
    val existingMessagesPath = MutableLiveData<DatabaseReference>()
    val messages = MutableLiveData<MutableList<Message>>()

    init {
        addMessagesListener()
    }

    fun getUserObjectById(id: String) {
        model.getUserObjectById(id, friendObject)
    }

    fun downloadFriendMainPhoto(id: String) {
        model.downloadFriendMainPhoto(friendPhotoUri, id)
    }

    fun addChatToChatsList(friendId: String) {
        model.addChatToChatsList(friendId)
    }

    fun getExistingMessagesApp(friendId: String) {
        model.getExistingMessagesPath(friendId, existingMessagesPath)
    }

    fun getCurrentUserId() = model.getCurrentUserUId()

    private fun addMessagesListener() {
        existingMessagesPath.value?.let { model.addMessagesListener(it, messages) }
    }

    fun sendMessage(message: Message, messageId: String) {
        existingMessagesPath.value?.let { model.sendMessage(message, messageId, it) }
    }
}
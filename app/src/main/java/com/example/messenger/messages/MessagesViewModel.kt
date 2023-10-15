package com.example.messenger.messages

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class MessagesViewModel @Inject constructor(private val model: Model): ViewModel() {
    val friendObject = MutableLiveData<User>()
    val friendPhotoUri = MutableLiveData<Uri>()
    val existingMessagesPath = MutableLiveData<DatabaseReference>()
    val messages = MutableLiveData<MutableList<Message>>()

    fun getUserObjectById(id: String) {
        model.getUserObjectById(id, friendObject)
    }

    fun downloadFriendMainPhoto(id: String) {
        model.downloadFriendMainPhoto(friendPhotoUri, id)
    }

    fun addChatToChatsList(friendId: String) {
        model.addChatToChatsList(friendId)
    }

    fun getExistingMessagesPath(friendId: String) {
        model.getExistingMessagesPath(friendId, existingMessagesPath)
    }

    fun addMessagesListener() {
        existingMessagesPath.value?.let { model.addMessagesListener(it, messages) }
    }

    fun sendMessage(message: Message, messageId: String) {
        existingMessagesPath.value?.let { model.sendMessage(message, messageId, it) }
    }

    fun getCurrentUserUId() = model.getCurrentUserUId()
}
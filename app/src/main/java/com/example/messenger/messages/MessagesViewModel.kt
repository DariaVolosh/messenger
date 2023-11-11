package com.example.messenger.messages

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.Message
import com.example.messenger.data.User

import com.example.messenger.domainLayer.AddChatToChatsListUseCase
import com.example.messenger.domainLayer.AddMessagesListenerUseCase
import com.example.messenger.domainLayer.GetConversationReferenceUseCase
import com.example.messenger.domainLayer.GetCurrentUserIdUseCase
import com.example.messenger.domainLayer.GetImageByUserIdUseCase
import com.example.messenger.domainLayer.GetUserObjectByIdUseCase
import com.example.messenger.domainLayer.SendMessageUseCase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val addChatToChatsListUseCase: AddChatToChatsListUseCase,
    private val addMessagesListenerUseCase: AddMessagesListenerUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserObjectByIdUseCase: GetUserObjectByIdUseCase,
    private val getImageByUserIdUseCase: GetImageByUserIdUseCase,
    private val getConversationReferenceUseCase: GetConversationReferenceUseCase,
    private val sendMessageUseCase: SendMessageUseCase
): ViewModel() {
    val friendObject = MutableLiveData<User>()
    val friendPhotoUri = MutableLiveData<Uri>()
    val existingMessagesPath = MutableLiveData<DatabaseReference>()
    val messages = MutableLiveData<List<Message>>()
    val currentUserId = MutableLiveData<String>()

    init {
        getCurrentUserId()
    }

    private fun getCurrentUserId() {
        getCurrentUserIdUseCase.getCurrentUserId()?.let { id ->
            currentUserId.value = id
        }
    }

    fun getUserObjectById(id: String) {
        viewModelScope.launch {
            val fetchedFriend = getUserObjectByIdUseCase.getUserById(id).await()
            friendObject.value = fetchedFriend
        }
    }

    fun downloadFriendMainPhoto(id: String) {
        viewModelScope.launch {
            val fetchedUri = getImageByUserIdUseCase.getImageById(id).await()
            friendPhotoUri.value = fetchedUri
        }
    }

    fun addChatToChatsList(friendId: String) {
        currentUserId.value?.let { currentUserId ->
            addChatToChatsListUseCase.addChatToChatsList(currentUserId, friendId)
        }
    }

    fun getExistingMessagesPath(friendId: String) {
        currentUserId.value?.let { currentUserId ->
            existingMessagesPath.value =
                getConversationReferenceUseCase.getConversationReference(currentUserId, friendId)
        }
    }

    fun addMessagesListener() {
        existingMessagesPath.value?.let { existingMessagesPath ->
            val list = mutableListOf<Message>()
            viewModelScope.launch {
                val flow =
                    addMessagesListenerUseCase.addMessagesListener(existingMessagesPath)

                flow.collect {message ->
                    if (message.timestamp.toInt() == 0) {
                        messages.value = list
                    } else list.add(message)
                }
            }
        }
    }

    fun sendMessage(message: Message) {
        existingMessagesPath.value?.let { existingMessagesPath ->
            sendMessageUseCase.sendMessage(message, existingMessagesPath)
        }
    }
}
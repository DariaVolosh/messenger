package com.example.messenger.presenter.messages

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.domain.chats.AddChatToChatsListUseCase
import com.example.messenger.domain.chats.GetConversationReferenceUseCase
import com.example.messenger.domain.image.GetImageByUserIdUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.messages.AddMessagesListenerUseCase
import com.example.messenger.domain.messages.GetMessagesFlowUseCase
import com.example.messenger.domain.messages.SendMessageUseCase
import com.example.messenger.domain.user.EmitMessagesOnlineStatusUseCase
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import com.example.messenger.domain.user.GetOnlineStatusMessagesFlowUseCase
import com.example.messenger.domain.user.GetUserObjectByIdUseCase
import com.example.messenger.domain.user.GetUserOnlineStatusUseCase
import com.example.messenger.domain.userSettings.GetAndSaveMessagesColorUseCase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val addChatToChatsListUseCase: AddChatToChatsListUseCase,
    private val addMessagesListenerUseCase: AddMessagesListenerUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val getUserObjectByIdUseCase: GetUserObjectByIdUseCase,
    private val getImageByUserIdUseCase: GetImageByUserIdUseCase,
    private val getConversationReferenceUseCase: GetConversationReferenceUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val loadImageUseCase: LoadImageUseCase,
    private val getAndSaveMessagesColorUseCase: GetAndSaveMessagesColorUseCase,
    private val getMessagesFlowUseCase: GetMessagesFlowUseCase,
    private val getUserOnlineStatusUseCase: GetUserOnlineStatusUseCase,
    private val getOnlineStatusMessagesFlowUseCase: GetOnlineStatusMessagesFlowUseCase,
    private val emitMessagesOnlineStatusUseCase: EmitMessagesOnlineStatusUseCase
): ViewModel() {
    val friendObject = MutableLiveData<User>()
    val friendPhotoUri = MutableLiveData<Uri>()
    val existingMessagesPath = MutableLiveData<DatabaseReference>()
    val messages = MutableLiveData<List<Message>>()
    val currentUser = MutableLiveData<User>()
    val onlineStatus = MutableLiveData<Boolean>()

    fun getCurrentUserObject() {
        viewModelScope.launch {
            currentUser.value = getCurrentUserObjectUseCase.getCurrentUserObject()
        }
    }

    fun getUserObjectById(id: String) {
        viewModelScope.launch {
            val fetchedFriend = getUserObjectByIdUseCase.getUserById(id)
            friendObject.value = fetchedFriend
            friendObject.value?.let {friend ->
                collectOnlineStatus(id)
                getExistingMessagesPath(friend.userId)
            }
        }
    }

    fun downloadFriendMainPhoto(id: String) {
        viewModelScope.launch {
            val fetchedUri = getImageByUserIdUseCase.getImageById(id)
            friendPhotoUri.value = fetchedUri
        }
    }

    fun addChatToChatsList(friendId: String) {
        viewModelScope.launch {
            currentUser.value?.let {user ->
                addChatToChatsListUseCase.addChatToChatsList(user.userId, friendId)
            }
        }
    }

    private fun getExistingMessagesPath(friendId: String) {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                existingMessagesPath.value =
                    getConversationReferenceUseCase.getConversationReference(user.userId, friendId)
                addMessagesListener()
            }
        }
    }

    private fun addMessagesListener() {
        existingMessagesPath.value?.let { existingMessagesPath ->
            val list = mutableListOf<Message>()
            val flow =
                getMessagesFlowUseCase.getMessagesFlowUseCase()

            viewModelScope.launch {
                messages.postValue(emptyList())

                flow.collect {message ->
                    list.add(message)
                    messages.value = list.sortedBy { it.timestamp }.toMutableList()
                }
            }

            viewModelScope.launch {
                addMessagesListenerUseCase.addMessagesListener(existingMessagesPath)
            }
        }
    }


    fun sendMessage(message: Message) {
        viewModelScope.launch {
            existingMessagesPath.value?.let { existingMessagesPath ->
                sendMessageUseCase.sendMessage(message, existingMessagesPath)
            }
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }

    suspend fun getMessagesColor(key: String) =
        withContext(Dispatchers.IO) {
            getAndSaveMessagesColorUseCase.getMessagesColor(key)
        }

    private fun collectOnlineStatus(id: String) {
        val flow = getOnlineStatusMessagesFlowUseCase.getOnlineStatusMessagesFlow()
        emitMessagesOnlineStatusUseCase.emitMessagesOnlineStatusUseCase(id)

        viewModelScope.launch {
            onlineStatus.value = getUserOnlineStatusUseCase.getUserOnlineStatus(id)
            flow.collect {online ->
                onlineStatus.value = online
            }
        }
    }
}
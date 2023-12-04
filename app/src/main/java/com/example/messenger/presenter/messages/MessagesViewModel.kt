package com.example.messenger.presenter.messages

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.domain.chats.AddChatToChatsListUseCase
import com.example.messenger.domain.messages.AddMessagesListenerUseCase
import com.example.messenger.domain.user.EmitOnlineValuesUseCase
import com.example.messenger.domain.userSettings.GetAndSaveMessagesColorUseCase
import com.example.messenger.domain.chats.GetConversationReferenceUseCase
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import com.example.messenger.domain.image.GetImageByUserIdUseCase
import com.example.messenger.domain.user.GetOnlineFlowById
import com.example.messenger.domain.user.GetOnlineStatusFlowListUseCase
import com.example.messenger.domain.user.GetUserObjectByIdUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.messages.SendMessageUseCase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
    private val getOnlineStatusFlowListUseCase: GetOnlineStatusFlowListUseCase,
    private val emitOnlineValuesUseCase: EmitOnlineValuesUseCase,
    private val getOnlineFlowById: GetOnlineFlowById
): ViewModel() {
    val friendObject = MutableLiveData<User>()
    val friendPhotoUri = MutableLiveData<Uri>()
    val existingMessagesPath = MutableLiveData<DatabaseReference>()
    val messages = MutableLiveData<List<Message>>()
    val currentUser = MutableLiveData<User>()
    val onlineStatus = MutableLiveData<List<Flow<Boolean>>>()

    init {
        getCurrentUserObject()
    }

    private fun getCurrentUserObject() {
        viewModelScope.launch {
            currentUser.value = getCurrentUserObjectUseCase.getCurrentUserObject()
        }
    }

    fun getUserObjectById(id: String) {
        viewModelScope.launch {
            val fetchedFriend = getUserObjectByIdUseCase.getUserById(id)
            friendObject.value = fetchedFriend
            friendObject.value?.let {
                getOnlineUserStatusFlowList()
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

    fun getExistingMessagesPath(friendId: String) {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                existingMessagesPath.value =
                    getConversationReferenceUseCase.getConversationReference(user.userId, friendId)
            }
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
        viewModelScope.launch {
            existingMessagesPath.value?.let { existingMessagesPath ->
                sendMessageUseCase.sendMessage(message, existingMessagesPath)
            }
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        loadImageUseCase.loadImage(uri, imageView)
    }

    fun getMessagesColor(key: String) = getAndSaveMessagesColorUseCase.getMessagesColor(key)

    private fun getOnlineUserStatusFlowList() {
        viewModelScope.launch {
            friendObject.value?.let { friend ->
                onlineStatus.value = getOnlineStatusFlowListUseCase.getOnlineStatusFlowList(
                    listOf(friend)
                )
            }
        }
    }

    fun emitOnlineStatus(list: List<Flow<Boolean>>) {
        friendObject.value?.let {friend ->
            emitOnlineValuesUseCase.emitOnlineValues(list, listOf(friend))
        }
    }

    fun getFlowById(id: String): Flow<Boolean> = getOnlineFlowById.getOnlineFlowById(id)
}
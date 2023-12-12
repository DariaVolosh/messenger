package com.example.messenger.presenter.chats

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.domain.chats.FetchChatsUseCase
import com.example.messenger.domain.chats.GetChatsFlowUseCase
import com.example.messenger.domain.image.DownloadImagesUseCase
import com.example.messenger.domain.image.GetMyImageUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.messages.FetchLastMessagesUseCase
import com.example.messenger.domain.messages.GetLastMessagesFlowUseCase
import com.example.messenger.domain.user.EmitChatsOnlineValuesUseCase
import com.example.messenger.domain.user.GetOnlineStatusListFlowUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatsViewModel @Inject constructor(
    private val fetchChatsUseCase: FetchChatsUseCase,
    private val getMyImageUseCase: GetMyImageUseCase,
    private val fetchLastMessagesUseCase: FetchLastMessagesUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val loadImageUseCase: LoadImageUseCase,
    private val emitChatsOnlineValuesUseCase: EmitChatsOnlineValuesUseCase,
    private val getChatsFlowUseCase: GetChatsFlowUseCase,
    private val getLastMessagesFlowUseCase: GetLastMessagesFlowUseCase,
    private val getOnlineStatusListFlowUseCase: GetOnlineStatusListFlowUseCase
) : ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    var chatList = MutableLiveData<List<User>>()
    var lastMessages = MutableLiveData<MutableList<Message>>()
    var photoUris = MutableLiveData<List<Uri>>()
    var onlineStatus = MutableLiveData<MutableList<Boolean>>()

    fun fetchChats() {
        val flow = getChatsFlowUseCase.getChatsFlow()
        viewModelScope.launch {
            flow.collect {list ->
                chatList.value = list
                downloadImages(list)
                fetchLastMessages(list)
                collectOnlineStatus()
            }
        }

       viewModelScope.launch {
           fetchChatsUseCase.fetchChats()
       }
    }

    fun downloadImage() {
        viewModelScope.launch {
            val uri = getMyImageUseCase.getMyImage()
            mainPhotoUri.value = uri
        }
    }

    private fun downloadImages(list: List<User>) {
        viewModelScope.launch {
            val uris = downloadImagesUseCase.getImages(list)
            photoUris.value = uris
        }
    }

    private fun fetchLastMessages(chats: List<User>) {
        viewModelScope.launch {
            val messagesFlow = getLastMessagesFlowUseCase.getLastMessagesFlow()
            messagesFlow.collect {messages ->
                lastMessages.value = mutableListOf()
                lastMessages.value = messages.toMutableList()
            }
        }

        viewModelScope.launch {
            if (lastMessages.value == null) {
                fetchLastMessagesUseCase.fetchLastMessages(chats, mutableListOf())
            }

            lastMessages.value?.let {
                fetchLastMessagesUseCase.fetchLastMessages(chats, it)
            }
        }
    }

    private suspend fun collectOnlineStatus() {
        val onlineStatusList = getOnlineStatusListFlowUseCase.getOnlineStatusListFlow()
        viewModelScope.launch {
            onlineStatusList.collect {online ->
                onlineStatus.value = mutableListOf()
                onlineStatus.value = online.toMutableList()
            }
        }

        chatList.value?.let { users ->
            viewModelScope.launch {
                if (onlineStatus.value == null) {
                    emitChatsOnlineValuesUseCase.emitOnlineValues(users, mutableListOf())
                } else {
                    onlineStatus.value?.let {online ->
                        emitChatsOnlineValuesUseCase.emitOnlineValues(users, online)
                    }
                }
            }
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }
}
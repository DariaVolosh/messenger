package com.example.messenger.presenter.chats

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import com.example.messenger.domain.image.DownloadImagesUseCase
import com.example.messenger.domain.chats.FetchChatsUseCase
import com.example.messenger.domain.messages.FetchLastMessagesUseCase
import com.example.messenger.domain.image.GetMyImageUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatsViewModel @Inject constructor(
    private val fetchChatsUseCase: FetchChatsUseCase,
    private val getMyImageUseCase: GetMyImageUseCase,
    private val fetchLastMessagesUseCase: FetchLastMessagesUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val loadImageUseCase: LoadImageUseCase,
    private val userRepository: UserRepository
) : ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    val mainPhotoBitmap = MutableLiveData<Bitmap>()
    var chatList = MutableLiveData<List<User>>()
    var lastMessages = MutableLiveData<List<Message>>()
    var photoUris = MutableLiveData<List<Uri>>()
    var onlineStatus = MutableLiveData<List<Flow<Boolean>>>()

    init {
        fetchChats()
        downloadImage()
    }


    private fun fetchChats() {
        viewModelScope.launch {
            val list = fetchChatsUseCase.fetchChats()
            chatList.value = list
            downloadImages(list)
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            val uri = getMyImageUseCase.getMyImage()
            mainPhotoUri.value = uri
        }
    }

    private fun downloadImages(list: List<User>) {
        viewModelScope.launch {
            val uris = downloadImagesUseCase.getImages(list)
            photoUris.value = uris
            fetchLastMessages(chatList.value ?: listOf())
        }
    }

    private fun fetchLastMessages(chats: List<User>) {
        viewModelScope.launch {
            val lastMessagesList = fetchLastMessagesUseCase.fetchLastMessages(chats)
            lastMessages.value = lastMessagesList
            chatList.value?.let { users ->
                getOnlineUserStatusFlowList(users)
            }
        }
    }

    private fun getOnlineUserStatusFlowList(list: List<User>) {
        onlineStatus.value = userRepository.getOnlineUserStatusFlowList(list)
    }

    fun emitOnlineStatus(list: List<Flow<Boolean>>) {
        chatList.value?.let { users ->
            userRepository.emitOnlineValues(list, users)
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }
}
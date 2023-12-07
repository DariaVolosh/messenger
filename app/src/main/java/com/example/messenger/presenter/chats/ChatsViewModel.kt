package com.example.messenger.presenter.chats

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.UserRepository
import com.example.messenger.domain.chats.FetchChatsUseCase
import com.example.messenger.domain.image.DownloadImagesUseCase
import com.example.messenger.domain.image.GetMyImageUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.messages.FetchLastMessagesUseCase
import com.example.messenger.domain.user.EmitOnlineValuesUseCase
import kotlinx.coroutines.flow.Flow
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
    private val userRepository: UserRepository,
    private val emitOnlineValuesUseCase: EmitOnlineValuesUseCase
) : ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    var chatList = MutableLiveData<List<User>>()
    var lastMessages = MutableLiveData<List<Message>>()
    var photoUris = MutableLiveData<List<Uri>>()
    var onlineStatus = MutableLiveData<MutableList<Boolean>>()

    fun fetchChats() {
        viewModelScope.launch {
            val list = fetchChatsUseCase.fetchChats()
            chatList.value = list
            downloadImages(list)
            fetchLastMessages(list)
            getOnlineUserStatusFlowList(list)
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
            val lastMessagesList = fetchLastMessagesUseCase.fetchLastMessages(chats)
            lastMessages.value = lastMessagesList
        }
    }

    private suspend fun getOnlineUserStatusFlowList(list: List<User>) {
        val onlineStatusList = userRepository.getOnlineUserStatusFlowList(list)
        collectOnlineStatus(onlineStatusList)
    }

    private suspend fun collectOnlineStatus(list: List<Flow<Boolean>>) {
        onlineStatus.value = MutableList(list.size) {false}
        viewModelScope.launch {
            for (i in list.indices) {
                list[i].collect {online ->
                    val newList = onlineStatus.value?.toMutableList()
                    newList?.set(i, online)
                    onlineStatus.value = newList ?: mutableListOf()
                }
            }
        }

        emitOnlineStatus(list)
    }

    private fun emitOnlineStatus(list: List<Flow<Boolean>>) {
        chatList.value?.let { users ->
            viewModelScope.launch {
                emitOnlineValuesUseCase.emitOnlineValues(list, users)
            }
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }
}
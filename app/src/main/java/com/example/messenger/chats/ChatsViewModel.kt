package com.example.messenger.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.example.messenger.dataLayer.NetworkUtils
import com.example.messenger.domainLayer.DownloadImagesUseCase
import com.example.messenger.domainLayer.FetchChatsUseCase
import com.example.messenger.domainLayer.FetchLastMessagesUseCase
import com.example.messenger.domainLayer.GetMyImageUseCase
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ChatsViewModel @Inject constructor(
    private val fetchChatsUseCase: FetchChatsUseCase,
    private val getMyImageUseCase: GetMyImageUseCase,
    private val fetchLastMessagesUseCase: FetchLastMessagesUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val networkUtils: NetworkUtils
) : ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    val mainPhotoBitmap = MutableLiveData<Bitmap>()
    val chatList = MutableLiveData<List<User>>()
    val lastMessages = MutableLiveData<List<Message>>()
    val photoUris = MutableLiveData<List<Uri>>()

    init {
        fetchChats()
        downloadImage()
    }

    private fun fetchChats() {
        viewModelScope.launch {
            val list = fetchChatsUseCase.fetchChats().await()
            chatList.value = list
            downloadImages(list)
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            val isInternetAvailable = networkUtils.isInternetAvailable()
            val uri = getMyImageUseCase.getMyImage(isInternetAvailable).await()
            if (isInternetAvailable) {
                mainPhotoUri.value = uri
            } else {
                val localFile = File(uri.toString())
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                mainPhotoBitmap.value = bitmap
            }
        }
    }

    private fun downloadImages(list: List<User>) {
        viewModelScope.launch {
            val uris = downloadImagesUseCase.getImages(list).await()
            photoUris.value = uris
            fetchLastMessages(chatList.value ?: listOf())
        }
    }

    private fun fetchLastMessages(chats: List<User>) {
        viewModelScope.launch {
            val lastMessagesList = fetchLastMessagesUseCase.fetchLastMessages(chats).await()
            lastMessages.value = lastMessagesList
        }
    }
}
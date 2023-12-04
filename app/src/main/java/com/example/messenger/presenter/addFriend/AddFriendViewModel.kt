package com.example.messenger.presenter.addFriend

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.image.DownloadImagesUseCase
import com.example.messenger.domain.user.GetCurrentUserIdUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.user.SearchUsersByLoginUseCase
import com.example.messenger.domain.user.UpdateUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddFriendViewModel @Inject constructor(
    private val searchUsersByLoginUseCase: SearchUsersByLoginUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val loadImageUseCase: LoadImageUseCase,
) : ViewModel() {
    val foundUsers = MutableLiveData<List<User>>()
    val images = MutableLiveData<List<Uri>>()
    val currentUserId = MutableLiveData<String>()

    init {
        getCurrentUserId()
    }

    fun searchUserByLogin(loginQuery: String) {
        viewModelScope.launch {
            val list = searchUsersByLoginUseCase.getUsersByLogin(loginQuery)
            foundUsers.value = list
        }
    }

    fun downloadImages(list: List<User>) {
        viewModelScope.launch {
            val uris = downloadImagesUseCase.getImages(list)
            images.value = uris
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            updateUserUseCase.updateUser(updatedUser)
        }
    }

    private fun getCurrentUserId() {
        viewModelScope.launch {
            currentUserId.value = getCurrentUserIdUseCase.getCurrentUserId()
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }
}
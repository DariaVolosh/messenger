package com.example.messenger.addFriend

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.User
import com.example.messenger.domainLayer.DownloadImagesUseCase
import com.example.messenger.domainLayer.GetCurrentUserIdUseCase
import com.example.messenger.domainLayer.SearchUsersByLoginUseCase
import com.example.messenger.domainLayer.UpdateUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddFriendViewModel @Inject constructor(
    private val searchUsersByLoginUseCase: SearchUsersByLoginUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {
    val foundUsers = MutableLiveData<List<User>>()
    val images = MutableLiveData<List<Uri>>()
    val currentUserId = MutableLiveData<String>()

    init {
        getCurrentUserId()
    }

    fun searchUserByLogin(loginQuery: String) {
        viewModelScope.launch {
            val list = searchUsersByLoginUseCase.getUsersByLogin(loginQuery).await()
            foundUsers.value = list
        }
    }

    fun downloadImages(list: List<User>) {
        viewModelScope.launch {
            val uris = downloadImagesUseCase.getImages(list).await()
            images.value = uris
        }
    }

    fun updateUser(updatedUser: User) {
        updateUserUseCase.updateUser(updatedUser)
    }

    private fun getCurrentUserId() {
        currentUserId.value = getCurrentUserIdUseCase.getCurrentUserId()
    }
}
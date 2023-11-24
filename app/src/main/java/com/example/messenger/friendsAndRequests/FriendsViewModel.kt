package com.example.messenger.friendsAndRequests

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.User
import com.example.messenger.domain.DownloadImagesUseCase
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import com.example.messenger.domain.GetUserObjectByIdUseCase
import com.example.messenger.domain.GetUsersByIdsUserCase
import com.example.messenger.domain.UpdateUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsViewModel @Inject constructor(
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val getUsersByIdsUserCase: GetUsersByIdsUserCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserObjectByIdUseCase: GetUserObjectByIdUseCase
): ViewModel() {
    val friendsAndRequestsList = MutableLiveData<List<User>>()
    val images = MutableLiveData<List<Uri>>()
    val friend = MutableLiveData<User>()
    val currentUser = MutableLiveData<User>()

    init {
        getCurrentUserObject()
    }

    private fun getCurrentUserObject() {
        viewModelScope.launch {
            currentUser.value = getCurrentUserObjectUseCase.currentUser.await()
        }
    }

    fun getUsersFromUId(list: List<String>) {
        viewModelScope.launch {
            val fetchedUsers = getUsersByIdsUserCase.getUsersByIds(list)
            friendsAndRequestsList.value = fetchedUsers
            downloadImages(fetchedUsers)
        }
    }

   private fun downloadImages(list: List<User>) {
       viewModelScope.launch {
           val fetchedImages = downloadImagesUseCase.getImages(list)
           images.value = fetchedImages
       }
   }

    fun updateUser(user: User) {
        viewModelScope.launch {
            updateUserUseCase.updateUser(user)
        }
    }

    fun getUserObjectById(id: String) {
        viewModelScope.launch {
            val fetchedFriend = getUserObjectByIdUseCase.getUserById(id)
            friend.value = fetchedFriend
        }
    }
}
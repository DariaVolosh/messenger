package com.example.messenger.friendsAndRequests

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.User
import com.example.messenger.domainLayer.DownloadImagesUseCase
import com.example.messenger.domainLayer.GetCurrentUserObjectUseCase
import com.example.messenger.domainLayer.GetUserObjectByIdUseCase
import com.example.messenger.domainLayer.GetUsersByIdsUserCase
import com.example.messenger.domainLayer.UpdateUserUseCase
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
            getCurrentUserObjectUseCase.currentUser?.let { deferred ->
                val user = deferred.await()
                currentUser.value = user
            }
        }
    }

    fun getUsersFromUId(list: List<String>) {
        viewModelScope.launch {
            val fetchedUsers = getUsersByIdsUserCase.getUsersByIds(list).await()
            friendsAndRequestsList.value = fetchedUsers
            downloadImages(fetchedUsers)
        }
    }

   private fun downloadImages(list: List<User>) {
       viewModelScope.launch {
           val fetchedImages = downloadImagesUseCase.getImages(list).await()
           images.value = fetchedImages
       }
   }

    fun updateUser(user: User) {
        updateUserUseCase.updateUser(user)
    }

    fun getUserObjectById(id: String) {
        viewModelScope.launch {
            val fetchedFriend = getUserObjectByIdUseCase.getUserById(id).await()
            friend.value = fetchedFriend
        }
    }
}
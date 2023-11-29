package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.DownloadImagesUseCase
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import com.example.messenger.domain.GetDataModelListFromUserListUseCase
import com.example.messenger.domain.GetUserListFromUserIdListUseCase
import com.example.messenger.domain.GetUserObjectByIdUseCase
import com.example.messenger.domain.LoadImageUseCase
import com.example.messenger.domain.UpdateUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsViewModel @Inject constructor(
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val getUserListFromUserIdListUseCase: GetUserListFromUserIdListUseCase,
    private val downloadImagesUseCase: DownloadImagesUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserObjectByIdUseCase: GetUserObjectByIdUseCase,
    private val getDataModelListFromUserListUseCase: GetDataModelListFromUserListUseCase,
    private val loadImageUseCase: LoadImageUseCase
): ViewModel() {
    val friendsAndRequestsList = MutableLiveData<List<User>>()
    val images = MutableLiveData<List<Uri>>()
    val friend = MutableLiveData<User>()
    val currentUser = MutableLiveData<User>()
    val chatOpened = MutableLiveData<Boolean>()
    val dataModelList = MutableLiveData<List<DataModel>>()

    init {
        getCurrentUserObject()
    }

    fun openChat() {
        chatOpened.value = true
        chatOpened.value = false
    }

    private fun getCurrentUserObject() {
        viewModelScope.launch {
            currentUser.value = getCurrentUserObjectUseCase.currentUser.await()
        }
    }

    fun getUsersFromUId(list: List<String>) {
        viewModelScope.launch {
            val fetchedUsers = getUserListFromUserIdListUseCase.userIdListToUserList(list)
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

    fun getDataModelListFromUserList(users: List<User>) {
        viewModelScope.launch {
            dataModelList.value = getDataModelListFromUserListUseCase.getDataModelListFromUserList(users)
        }
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        loadImageUseCase.loadImage(uri, imageView)
    }
}
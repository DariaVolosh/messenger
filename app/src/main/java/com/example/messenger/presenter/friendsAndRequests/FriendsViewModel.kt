package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.image.DownloadImagesUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import com.example.messenger.domain.user.GetUserListFromUserIdListUseCase
import com.example.messenger.domain.user.GetUserObjectByIdUseCase
import com.example.messenger.domain.user.UpdateUserUseCase
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
    private val loadImageUseCase: LoadImageUseCase
): ViewModel() {
    val images = MutableLiveData<List<Uri>>()
    var friend = MutableLiveData<User>()
    var currentUser = MutableLiveData<User>()
    val friendsList = MutableLiveData<List<User>>()
    val requestsList = MutableLiveData<List<User>>()
    val chatOpened = MutableLiveData<Boolean>()

    fun openChat() {
        chatOpened.value = true
        chatOpened.value = false
    }

    private fun getUsersList(friends: List<String>, requests: List<String>) {
        viewModelScope.launch {
            friendsList.value = getUserListFromUserIdListUseCase.userIdListToUserList(friends)
            requestsList.value = getUserListFromUserIdListUseCase.userIdListToUserList(requests)

            val requestsAndFriends =
                friendsList.value?.plus(requestsList.value ?: listOf()) ?: listOf()
            downloadImages(requestsAndFriends)
        }
    }

    fun getCurrentUserObject() {
        viewModelScope.launch {
            currentUser.value = getCurrentUserObjectUseCase.getCurrentUserObject()
            currentUser.value?.let { user ->
                getUsersList(user.friends, user.receivedFriendRequests)
            }
        }
    }

   private fun downloadImages(list: List<User>) {
       viewModelScope.launch {
           val fetchedImages = downloadImagesUseCase.getImages(list)
           images.value = listOf()
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

    fun loadImage(uri: Uri, imageView: ImageView) {
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }
}
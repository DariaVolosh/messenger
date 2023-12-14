package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.image.DownloadImagesUseCase
import com.example.messenger.domain.user.EmitFriendRequestsListUseCase
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import com.example.messenger.domain.user.GetFriendRequestsListFlowUseCase
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
    private val getFriendRequestsListFlowUseCase: GetFriendRequestsListFlowUseCase,
    private val emitFriendRequestsListUseCase: EmitFriendRequestsListUseCase
): ViewModel() {
    val friendsImages = MutableLiveData<List<Uri>>()
    val requestsImages = MutableLiveData<List<Uri>>()
    var friend = MutableLiveData<User>()
    var currentUser = MutableLiveData<User>()
    val friendsList = MutableLiveData<List<User>>()
    val requestsList = MutableLiveData<List<User>>()

    fun emitFriendRequests() {
        val requestsFlow = getFriendRequestsListFlowUseCase.getFriendRequestsListFlow()
        emitFriendRequestsListUseCase.emitFriendRequestsList()

        viewModelScope.launch {
            requestsFlow.collect {requests ->
                requestsList.value = requests
                requestsImages.value = downloadImagesUseCase.getImages(requests)
                getCurrentUserObject()
            }
        }
    }

    private fun getFriendsList(friends: List<String>) {
        viewModelScope.launch {
            friendsList.value = getUserListFromUserIdListUseCase.userIdListToUserList(friends)
            friendsList.value?.let {friends ->
                friendsImages.value = downloadImagesUseCase.getImages(friends)
            }
        }
    }

    fun getCurrentUserObject() {
        viewModelScope.launch {
            currentUser.value = getCurrentUserObjectUseCase.getCurrentUserObject()
            currentUser.value?.let { user ->
                getFriendsList(user.friends)
            }
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
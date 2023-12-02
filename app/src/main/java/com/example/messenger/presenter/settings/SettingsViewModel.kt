package com.example.messenger.presenter.settings

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.GetAndSaveMessagesColorUseCase
import com.example.messenger.domain.GetCurrentUserIdUseCase
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import com.example.messenger.domain.GetMyImageUseCase
import com.example.messenger.domain.LoadImageUseCase
import com.example.messenger.domain.SetUserOnlineStatusUseCase
import com.example.messenger.domain.SignOutUserUseCase
import com.example.messenger.room.model.UserEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getMyImageUseCase: GetMyImageUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val loadImageUseCase: LoadImageUseCase,
    private val getAndSaveMessagesColorUseCase: GetAndSaveMessagesColorUseCase,
    private val setUserOnlineStatusUseCase: SetUserOnlineStatusUseCase
): ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    val mainPhotoBitmap = MutableLiveData<Bitmap>()
    val currentUserId = MutableLiveData<String>()
    val currentFirebaseUser = MutableLiveData<User>()
    val currentRoomUser = MutableLiveData<UserEntity>()

    init {
        downloadImage()
        getCurrentUser()
        getCurrentUserId()
    }

    private fun getCurrentUserId() {
        viewModelScope.launch {
            getCurrentUserIdUseCase.getCurrentUserId()?.let {id ->
                currentUserId.value = id
            }
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            val uri = getMyImageUseCase.getMyImage()
            mainPhotoUri.value = uri
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            val currentUser = getCurrentUserObjectUseCase.getCurrentUserObject()
            currentFirebaseUser.value = currentUser
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            signOutUserUseCase.signOutUser()
        }

        setUserOnlineStatus(false)
    }

    fun loadImage(uri: Uri, imageView: ImageView) {
        loadImageUseCase.loadImage(uri, imageView)
    }

    fun setMessagesColor(color: Int, key: String) {
        getAndSaveMessagesColorUseCase.setMessagesColor(color, key)
    }

    fun getMessagesColor(key: String) = getAndSaveMessagesColorUseCase.getMessagesColor(key)

    private fun setUserOnlineStatus(boolean: Boolean) {
        currentUserId.value?.let { id ->
            setUserOnlineStatusUseCase.setOnlineStatus(boolean, id)
        }
    }
}
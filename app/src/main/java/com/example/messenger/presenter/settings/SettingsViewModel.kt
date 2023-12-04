package com.example.messenger.presenter.settings

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.userSettings.GetAndSaveMessagesColorUseCase
import com.example.messenger.domain.user.GetCurrentUserIdUseCase
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import com.example.messenger.domain.image.GetMyImageUseCase
import com.example.messenger.domain.image.LoadImageUseCase
import com.example.messenger.domain.user.SetUserOnlineStatusUseCase
import com.example.messenger.domain.user.SignOutUserUseCase
import com.example.messenger.room.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        viewModelScope.launch {
            loadImageUseCase.loadImage(uri, imageView)
        }
    }

    fun setMessagesColor(color: Int, key: String) {
        viewModelScope.launch {
            getAndSaveMessagesColorUseCase.setMessagesColor(color, key)
        }
    }

    suspend fun getMessagesColor(key: String) = withContext(Dispatchers.IO) {
        getAndSaveMessagesColorUseCase.getMessagesColor(key)
    }

    private fun setUserOnlineStatus(boolean: Boolean) {
        viewModelScope.launch {
            currentUserId.value?.let { id ->
                setUserOnlineStatusUseCase.setOnlineStatus(boolean, id)
            }
        }
    }
}
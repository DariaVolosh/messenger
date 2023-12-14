package com.example.messenger.presenter.settings

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.image.GetMyImageUseCase
import com.example.messenger.domain.user.GetCurrentUserIdUseCase
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import com.example.messenger.domain.user.SetUserOnlineStatusUseCase
import com.example.messenger.domain.user.SignOutUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getMyImageUseCase: GetMyImageUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val setUserOnlineStatusUseCase: SetUserOnlineStatusUseCase
): ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    val currentUserId = MutableLiveData<String>()
    val currentUser = MutableLiveData<User>()

    private fun downloadImage() {
        viewModelScope.launch {
            val uri = getMyImageUseCase.getMyImage()
            mainPhotoUri.value = uri
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val currUser = getCurrentUserObjectUseCase.getCurrentUserObject()
            currentUser.value = currUser
            downloadImage()
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            signOutUserUseCase.signOutUser()
        }

        setUserOnlineStatus(false)
    }

    private fun setUserOnlineStatus(boolean: Boolean) {
        viewModelScope.launch {
            currentUserId.value?.let { id ->
                setUserOnlineStatusUseCase.setOnlineStatus(boolean, id)
            }
        }
    }
}
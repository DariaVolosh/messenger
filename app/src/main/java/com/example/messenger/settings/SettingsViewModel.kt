package com.example.messenger.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.NetworkUtils
import com.example.messenger.data.User
import com.example.messenger.domain.GetCurrentUserIdUseCase
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import com.example.messenger.domain.GetMyImageUseCase
import com.example.messenger.domain.GetRoomUserEntityByIdUseCase
import com.example.messenger.room.UserEntity
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getMyImageUseCase: GetMyImageUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getRoomUserEntityByIdUseCase: GetRoomUserEntityByIdUseCase,
    private val networkUtils: NetworkUtils,
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
            val isInternetAvailable = networkUtils.isInternetAvailable()
            val uri = getMyImageUseCase.getMyImage(isInternetAvailable)
            if (isInternetAvailable) {
                mainPhotoUri.value = uri
            } else {
                val localFile = File(uri.toString())
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                mainPhotoBitmap.value = bitmap
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            if (networkUtils.isInternetAvailable()) {
                val currentUser = getCurrentUserObjectUseCase.currentUser.await()
                currentFirebaseUser.value = currentUser
            } else {
                currentUserId.value?.let { id ->
                    currentRoomUser.value = getRoomUserEntityByIdUseCase.getUserEntityById(id)
                }
            }
        }
    }
}
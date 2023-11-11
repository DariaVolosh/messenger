package com.example.messenger.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.User
import com.example.messenger.dataLayer.NetworkUtils
import com.example.messenger.domainLayer.GetCurrentUserIdUseCase
import com.example.messenger.domainLayer.GetCurrentUserObjectUseCase
import com.example.messenger.domainLayer.GetMyImageUseCase
import com.example.messenger.domainLayer.GetRoomUserEntityByIdUseCase
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
        getCurrentUserIdUseCase.getCurrentUserId()?.let {id ->
            currentUserId.value = id
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            val isInternetAvailable = networkUtils.isInternetAvailable()
            val uri = getMyImageUseCase.getMyImage(isInternetAvailable).await()
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
                val currentUser = getCurrentUserObjectUseCase.currentUser?.await()
                currentUser?.let { currentFirebaseUser.value = it }
            } else {
                currentUserId.value?.let { id ->
                    val roomUser = getRoomUserEntityByIdUseCase.getUserEntityById(id).await()
                    roomUser?.let { currentRoomUser.value = it }
                }
            }
        }
    }
}
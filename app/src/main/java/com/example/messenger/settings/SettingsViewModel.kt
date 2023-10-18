package com.example.messenger.settings

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.Model
import com.example.messenger.room.UserEntity
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val model: Model): ViewModel() {
    val mainPhotoUri = MutableLiveData<Uri>()
    val mainPhotoBitmap = MutableLiveData<Bitmap>()
    fun downloadImage(lifecycleOwner: LifecycleOwner) {
        model.downloadMyMainPhoto(mainPhotoUri, mainPhotoBitmap,lifecycleOwner)
    }
    fun isInternetAvailable() = model.isInternetAvailable()
    fun getCurrentRoomUser(): LiveData<UserEntity> = model.getCurrentRoomUser()
    fun getCurrentFirebaseUser() = model.currentUserObject

    fun getCurrentUserId() = model.getCurrentUserUId()
}
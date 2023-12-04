package com.example.messenger.domain.image

import android.net.Uri
import com.example.messenger.data.model.User
import com.example.messenger.presenter.mapper.UserListToPhotoUriListMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadImagesUseCase @Inject constructor(
    private val userListToPhotoUriListMapper: UserListToPhotoUriListMapper
) {
    suspend fun getImages(list: List<User>): List<Uri> =
        withContext(Dispatchers.IO) {
            userListToPhotoUriListMapper.map(list)
        }
}
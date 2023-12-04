package com.example.messenger.domain.image

import android.net.Uri
import com.example.messenger.data.model.User
import com.example.messenger.presenter.mapper.UserListToPhotoUriListMapper
import javax.inject.Inject

class DownloadImagesUseCase @Inject constructor(
    private val userListToPhotoUriListMapper: UserListToPhotoUriListMapper
) {
    suspend fun getImages(list: List<User>): List<Uri> = userListToPhotoUriListMapper.map(list)
}
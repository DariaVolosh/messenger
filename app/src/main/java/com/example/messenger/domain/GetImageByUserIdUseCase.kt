package com.example.messenger.domain

import android.net.Uri
import com.example.messenger.presenter.mapper.UserToPhotoUriMapper
import javax.inject.Inject

class GetImageByUserIdUseCase @Inject constructor(
    private val userToPhotoUriMapper: UserToPhotoUriMapper
) {
    suspend fun getImageById(id: String): Uri =
        userToPhotoUriMapper.map(id)
}
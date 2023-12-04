package com.example.messenger.domain.image

import android.net.Uri
import com.example.messenger.presenter.mapper.UserToPhotoUriMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetImageByUserIdUseCase @Inject constructor(
    private val userToPhotoUriMapper: UserToPhotoUriMapper
) {
    suspend fun getImageById(id: String): Uri =
        withContext(Dispatchers.IO) {
            userToPhotoUriMapper.map(id)
        }
}
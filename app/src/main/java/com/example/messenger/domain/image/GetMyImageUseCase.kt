package com.example.messenger.domain.image

import android.net.Uri
import com.example.messenger.data.repositories.FirebaseImages
import com.example.messenger.domain.user.GetCurrentUserObjectUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMyImageUseCase @Inject constructor(
    private val imagesRepository: FirebaseImages,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    suspend fun getMyImage(): Uri =
        withContext(Dispatchers.IO) {
            val currentUser = getCurrentUserObjectUseCase.getCurrentUserObject()
            var result = imagesRepository.getMyImageUri(currentUser)

            result
        }
}
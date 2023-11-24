package com.example.messenger.domain

import android.net.Uri
import com.example.messenger.data.FirebaseImages
import com.example.messenger.data.User
import com.example.messenger.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMyImageUseCase @Inject constructor(
    private val imagesRepository: FirebaseImages,
    private val userRepository: UserRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    suspend fun getMyImage(internetAvailable: Boolean): Uri =
        withContext(Dispatchers.IO) {
            val currentUser: User? = if (internetAvailable) {
                getCurrentUserObjectUseCase.currentUser.await()
            } else {
                val userId = userRepository.getCurrentUserId()
                userId?.let {
                    User(
                        "", "", "",
                        it,
                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf()
                    )
                }
            }

            val result = currentUser?.let {
                imagesRepository.getMyImageUri(it, internetAvailable)
            }

            result ?: Uri.parse("")
        }
}
package com.example.messenger.domain

import android.net.Uri
import com.example.messenger.data.ImagesRepository
import com.example.messenger.data.User
import com.example.messenger.data.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val imagesRepository: ImagesRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val signOutUserUseCase: SignOutUserUseCase
){
    suspend fun signUpUser(user: User, password: String, photoUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val userCreated = userRepository.signUpUser(user, password, photoUri)
            val userRegistered: CompletableDeferred<Boolean> = CompletableDeferred()

            if (userCreated) {
                getCurrentUserObjectUseCase.getCurrentUserObject()

                val imageUploaded = imagesRepository.uploadPhoto(photoUri, user)
                if (!imageUploaded) {
                    signOutUserUseCase.signOutUser()
                    getCurrentUserObjectUseCase.getCurrentUserObject()
                    userRegistered.complete(false)
                } else {
                    userRegistered.complete(true)
                }
            }

            userRegistered.await()
        }
}
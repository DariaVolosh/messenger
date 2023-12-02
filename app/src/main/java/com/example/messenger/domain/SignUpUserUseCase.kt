package com.example.messenger.domain

import android.net.Uri
import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.ImagesRepository
import com.example.messenger.data.repositories.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val imagesRepository: ImagesRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val setUserOnlineStatusUseCase: SetUserOnlineStatusUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
){
    suspend fun signUpUser(user: User, password: String, photoUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val userCreated = userRepository.signUpUser(user, password, photoUri)
            val userRegistered: CompletableDeferred<Boolean> = CompletableDeferred()

            if (userCreated) {
                val imageUploaded = imagesRepository.uploadPhoto(photoUri, user)
                if (!imageUploaded) {
                    signOutUserUseCase.signOutUser()
                    getCurrentUserObjectUseCase.getCurrentUserObject()
                    userRegistered.complete(false)
                } else {
                    userRegistered.complete(true)
                    getCurrentUserIdUseCase.getCurrentUserId()?.let {id ->
                        setUserOnlineStatusUseCase.setOnlineStatus(true, id)
                    }
                }
            }

            userRegistered.await()
        }
}
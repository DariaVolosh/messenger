package com.example.messenger.domainLayer

import android.net.Uri
import com.example.messenger.data.User
import com.example.messenger.dataLayer.UserRepository
import javax.inject.Inject

class SignUpUserUseCase @Inject
constructor(
    private val userRepository: UserRepository
){
    fun signUpUser(user: User, password: String, photoUri: Uri, uploadPhotoCallback: () -> Unit,
                   updateCurrentUserObjectCallback: () -> Unit) {
        userRepository.signUpUser(user, password, photoUri,
            uploadPhotoCallback, updateCurrentUserObjectCallback)
    }
}
package com.example.messenger.signUp

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.messenger.data.User
import com.example.messenger.domainLayer.GetCurrentUserObjectUseCase
import com.example.messenger.domainLayer.SignUpUserUseCase
import com.example.messenger.domainLayer.UploadPhotoUseCase
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val createUserUseCase: SignUpUserUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
): ViewModel() {
    fun signUpUser(user: User, password: String, photoUri: Uri) {
        createUserUseCase.signUpUser(user, password, photoUri,
            { uploadPhotoUseCase.uploadPhoto(photoUri, user)}
        )
        {getCurrentUserObjectUseCase.getCurrentUserObject() }
    }
}
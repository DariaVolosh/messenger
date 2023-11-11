package com.example.messenger.signIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.domainLayer.GetCurrentUserObjectUseCase
import com.example.messenger.domainLayer.SignInUserUseCase
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val signInUserUseCase: SignInUserUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
): ViewModel() {
    val currentUser = MutableLiveData<FirebaseUser?>()

    init {
        getCurrentUserObject()
    }

    private fun getCurrentUserObject() {
        currentUser.value = getCurrentUserObjectUseCase.getFirebaseUser()
    }
    fun signInUser(email: String, password: String) {
        signInUserUseCase.signInUser(email, password)
    }
}
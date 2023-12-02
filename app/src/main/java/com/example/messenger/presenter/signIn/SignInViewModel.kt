package com.example.messenger.presenter.signIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import com.example.messenger.domain.SignInUserUseCase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val signInUserUseCase: SignInUserUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
): ViewModel() {
    val currentUser = MutableLiveData<FirebaseUser?>()
    val signedIn = MutableLiveData<Boolean>()

    init {
        getCurrentFirebaseUser()
    }

    private fun getCurrentFirebaseUser() {
        currentUser.value = getCurrentUserObjectUseCase.getFirebaseUser()
    }

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            signedIn.value = signInUserUseCase.signInUser(email, password)
        }
    }
}
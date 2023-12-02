package com.example.messenger.presenter.signUp

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.model.User
import com.example.messenger.domain.GetCurrentUserObjectUseCase
import com.example.messenger.domain.SignUpUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val createUserUseCase: SignUpUserUseCase,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
): ViewModel() {
    val signedUp =  MutableLiveData<Boolean>()
    fun signUpUser(user: User, password: String, photoUri: Uri) {
        viewModelScope.launch {
            val signedUpOrError = createUserUseCase.signUpUser(user, password, photoUri)
            signedUp.value = signedUpOrError
        }
    }
}
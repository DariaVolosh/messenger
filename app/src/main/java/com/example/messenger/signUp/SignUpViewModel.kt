package com.example.messenger.signUp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messenger.Model
import javax.inject.Inject

class SignUpViewModel @Inject constructor(private val model: Model): ViewModel() {
    fun createUser(email: String, login: String, fullName: String, password: String, photoUri: Uri,
                   navController: NavController) {
        model.createUser(email, login, fullName, password, photoUri, navController)
    }
}
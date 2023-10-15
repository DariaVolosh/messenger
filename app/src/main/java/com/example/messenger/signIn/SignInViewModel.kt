package com.example.messenger.signIn

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messenger.Model
import javax.inject.Inject

class SignInViewModel @Inject constructor(private val model: Model): ViewModel() {

    fun signInUser(email: String, password: String, navController: NavController) {
        model.signInUser(email, password, navController)
    }
}
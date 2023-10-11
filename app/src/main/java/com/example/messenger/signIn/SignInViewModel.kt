package com.example.messenger.signIn

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messenger.Model
import com.example.messenger.MyApp

class SignInViewModel(val app: MyApp): ViewModel() {
    private val model = Model(app)

    fun signInUser(email: String, password: String, navController: NavController) {
        model.signInUser(email, password, navController)
    }
}
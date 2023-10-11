package com.example.messenger.signUp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messenger.Model
import com.example.messenger.MyApp

class SignUpViewModel(val app: MyApp): ViewModel() {
    private val model = Model(app)

    fun createUser(email: String, login: String, fullName: String, password: String, photoUri: Uri,
                   navController: NavController) {
        model.createUser(email, login, fullName, password, photoUri, navController)
    }
}
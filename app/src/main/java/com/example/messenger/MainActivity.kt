package com.example.messenger

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messenger.presenter.chats.ChatsScreen
import com.example.messenger.presenter.chats.ChatsViewModel
import com.example.messenger.presenter.signIn.SignInScreen
import com.example.messenger.presenter.signIn.SignInViewModel
import com.example.messenger.presenter.signUp.SignUpScreen
import com.example.messenger.presenter.signUp.SignUpViewModel
import com.example.messenger.ui.theme.MessengerTheme
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var signInViewModel: SignInViewModel
    @Inject lateinit var signUpViewModel: SignUpViewModel
    @Inject lateinit var chatsViewModel: ChatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myApp = application as MyApp
        if (myApp.appComponent == null) myApp.createAppComponent(this, layoutInflater)
        myApp.appComponent?.inject(this)

        setContent {
            MessengerTheme {
                MainScreen(signInViewModel, signUpViewModel, chatsViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(
    signInViewModel: SignInViewModel,
    signUpViewModel: SignUpViewModel,
    chatsViewModel: ChatsViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signIn") {
        composable("signIn") {
            SignInScreen(signInViewModel, {
                navController.navigate("chats")
            }) {
                navController.navigate("signUp")
            }
        }

        composable("chats") {
            ChatsScreen(chatsViewModel)
        }

        composable("signUp") {
            SignUpScreen(signUpViewModel) {
                navController.navigate("chats")
            }
        }
    }
}
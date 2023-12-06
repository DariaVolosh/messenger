package com.example.messenger

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messenger.presenter.signIn.SignInScreen
import com.example.messenger.presenter.signIn.SignInViewModel
import com.example.messenger.ui.theme.MessengerTheme
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as MyApp).createAppComponent(this, layoutInflater)
        (application as MyApp).appComponent.inject(this)

        setContent {
            MessengerTheme {
                MainScreen(signInViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(signInViewModel: SignInViewModel) {
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
            ChatsScreen()
        }

        composable("signUp") {
            SignUpScreen()
        }
    }
}

@Composable
fun ChatsScreen() {

}

@Composable
fun SignUpScreen() {

}
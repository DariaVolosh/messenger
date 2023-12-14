package com.example.messenger

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.messenger.presenter.addFriend.AddFriend
import com.example.messenger.presenter.addFriend.AddFriendViewModel
import com.example.messenger.presenter.chats.ChatsScreen
import com.example.messenger.presenter.chats.ChatsViewModel
import com.example.messenger.presenter.friendsAndRequests.FriendsAndRequestsScreen
import com.example.messenger.presenter.friendsAndRequests.FriendsViewModel
import com.example.messenger.presenter.messages.MessagesScreen
import com.example.messenger.presenter.messages.MessagesViewModel
import com.example.messenger.presenter.settings.SettingsScreen
import com.example.messenger.presenter.settings.SettingsViewModel
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
    @Inject lateinit var friendsViewModel: FriendsViewModel
    @Inject lateinit var messagesViewModel: MessagesViewModel
    @Inject lateinit var addFriendViewModel: AddFriendViewModel
    @Inject lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myApp = application as MyApp
        if (myApp.appComponent == null) myApp.createAppComponent(this, layoutInflater)
        myApp.appComponent?.inject(this)

        setContent {
            MessengerTheme {
                MainScreen(
                    signInViewModel,
                    signUpViewModel,
                    chatsViewModel,
                    friendsViewModel,
                    messagesViewModel,
                    addFriendViewModel,
                    settingsViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    signInViewModel: SignInViewModel,
    signUpViewModel: SignUpViewModel,
    chatsViewModel: ChatsViewModel,
    friendsViewModel: FriendsViewModel,
    messagesViewModel: MessagesViewModel,
    addFriendViewModel: AddFriendViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "signIn"
    ) {
        composable("signIn") {
            SignInScreen(signInViewModel, {
                navController.navigate("chats")
            }) {
                navController.navigate("signUp")
            }
        }

        composable("friendsAndRequests") {
            FriendsAndRequestsScreen(friendsViewModel) { id, uri ->
                navController.navigate("messages/$id")
            }
        }

        composable("chats") {
            ChatsScreen(chatsViewModel, { route ->
                navController.navigate(route)
            }, { user ->
                navController.navigate("messages/${user.userId}")
            }) {
                navController.navigate("addFriend")
            }
        }

        composable("signUp") {
            SignUpScreen(signUpViewModel) {
                navController.navigate("chats")
            }
        }

        composable("messages/{userId}") { backStackEntry ->
            MessagesScreen(
                backStackEntry.arguments?.getString("userId") ?: "",
                messagesViewModel
            ) {
                navController.popBackStack()
            }
        }

        composable("addFriend") {
            AddFriend(addFriendViewModel)
        }

        composable("settings") {
            SettingsScreen(settingsViewModel)
        }
    }
}
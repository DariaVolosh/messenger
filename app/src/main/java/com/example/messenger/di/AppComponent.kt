package com.example.messenger.di

import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.messenger.addFriend.AddFriendFragment
import com.example.messenger.chats.ChatsFragment
import com.example.messenger.friends_and_requests.FriendsFragment
import com.example.messenger.messages.MessagesFragment
import com.example.messenger.settings.SettingsFragment
import com.example.messenger.signIn.SignInFragment
import com.example.messenger.signUp.SignUpFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RoomDatabaseModule::class, FirebaseModule::class])
@Singleton
interface AppComponent {
    fun inject(fragment: AddFriendFragment)
    fun inject(fragment: ChatsFragment)
    fun inject(fragment: SignInFragment)
    fun inject(fragment: FriendsFragment)
    fun inject(fragment: MessagesFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: SignUpFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance layoutInflater: LayoutInflater,
            @BindsInstance lifecycleOwner: LifecycleOwner,
            @BindsInstance navController: NavController
        ): AppComponent
    }
}
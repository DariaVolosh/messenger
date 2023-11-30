package com.example.messenger.di

import android.content.Context
import android.view.LayoutInflater
import com.example.messenger.di.modules.FirebaseModule
import com.example.messenger.di.modules.ImageLoaderModule
import com.example.messenger.di.modules.MapperModule
import com.example.messenger.di.modules.RepositoriesModule
import com.example.messenger.di.modules.RoomDatabaseModule
import com.example.messenger.di.modules.SharedPrefsModule
import com.example.messenger.di.modules.UserModule
import com.example.messenger.presenter.addFriend.AddFriendFragment
import com.example.messenger.presenter.chats.ChatsFragment
import com.example.messenger.presenter.friendsAndRequests.FriendsFragment
import com.example.messenger.presenter.messages.MessagesFragment
import com.example.messenger.presenter.settings.SettingsFragment
import com.example.messenger.presenter.signIn.SignInFragment
import com.example.messenger.presenter.signUp.SignUpFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    RoomDatabaseModule::class,
    FirebaseModule::class,
    RepositoriesModule::class,
    ImageLoaderModule::class,
    MapperModule::class,
    SharedPrefsModule::class,
    UserModule::class
])
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
            @BindsInstance layoutInflater: LayoutInflater
        ): AppComponent
    }
}
package com.example.messenger

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.messenger.di.AppComponent
import com.example.messenger.di.DaggerAppComponent
import com.example.messenger.domain.GetCurrentUserIdUseCase
import com.example.messenger.domain.SetUserOnlineStatusUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject


class MyApp @Inject constructor(

) : Application() {
    lateinit var appComponent: AppComponent
    @Inject lateinit var lifecycleObserver: AppLifecycleObserver

    fun createAppComponent(context: Context, layoutInflater: LayoutInflater) {
        appComponent = DaggerAppComponent.factory().create(context, layoutInflater)
        appComponent.inject(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }
}

class AppLifecycleObserver @Inject constructor(
    private val setUserOnlineStatusUseCase: SetUserOnlineStatusUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            getCurrentUserIdUseCase.getCurrentUserId()?.let {id ->
                setUserOnlineStatusUseCase.setOnlineStatus(true, id)
            }
        }

        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        owner.lifecycleScope.launch {
            getCurrentUserIdUseCase.getCurrentUserId()?.let {id ->
                setUserOnlineStatusUseCase.setOnlineStatus(false, id)
            }
        }
    }
}
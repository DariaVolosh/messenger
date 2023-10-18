package com.example.messenger
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import com.example.messenger.chats.ChatsFragment

class MyCustomIdlingResource(private val activity: AppCompatActivity): IdlingResource {
    private lateinit var resourceCallback: ResourceCallback
    private var isIdle = true
    private var loginPerformed = false

    override fun getName(): String = MyCustomIdlingResource::class.java.name

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        if (callback != null) {
            this.resourceCallback = callback
        }
    }

    fun setLoginPerformed(loginPerformed: Boolean) {
        this.loginPerformed = loginPerformed
    }

    override fun isIdleNow(): Boolean {
        val fragmentManager = activity.supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.nav_host)
        if (loginPerformed) {
            isIdle = fragment!!.childFragmentManager.fragments[0] is ChatsFragment
        }

        if (isIdle) resourceCallback.onTransitionToIdle()
        return isIdle
    }
}
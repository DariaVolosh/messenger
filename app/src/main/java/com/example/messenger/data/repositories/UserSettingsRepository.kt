package com.example.messenger.data.repositories

import android.content.SharedPreferences
import com.example.messenger.R
import javax.inject.Inject

interface UserSettingsRepository {
    fun getMessageColor(key: String): Int
    fun setMessageColor(color: Int, key: String)
}

class SharedPreferencesUserSettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
): UserSettingsRepository {
    override fun getMessageColor(key: String) =
        sharedPreferences.getInt(key, R.color.turquoise)


    override fun setMessageColor(color: Int, key: String) {
        sharedPreferences.edit().putInt(key, color).apply()
    }
}
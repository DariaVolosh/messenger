package com.example.messenger.domain.userSettings

import com.example.messenger.data.repositories.UserSettingsRepository
import javax.inject.Inject

class GetAndSaveMessagesColorUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    fun setMessagesColor(color: Int, key: String) {
        userSettingsRepository.setMessageColor(color, key)
    }

    fun getMessagesColor(key: String) = userSettingsRepository.getMessageColor(key)
}
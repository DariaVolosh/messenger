package com.example.messenger.domain.userSettings

import com.example.messenger.data.repositories.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAndSaveMessagesColorUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend fun setMessagesColor(color: Int, key: String) {
        withContext(Dispatchers.IO) {
            userSettingsRepository.setMessageColor(color, key)
        }
    }

    suspend fun getMessagesColor(key: String) =
        withContext(Dispatchers.IO) {
            userSettingsRepository.getMessageColor(key)
        }
}
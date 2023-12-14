package com.example.messenger.domain.messages

import com.example.messenger.data.repositories.MessagesRepository
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class RemoveMessagesListenerUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {
    fun removeMessagesListenerUseCase(existingMessagesPath: DatabaseReference) {
        messagesRepository.removeMessagesListener(existingMessagesPath)
    }
}
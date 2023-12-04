package com.example.messenger.domain.messages

import com.example.messenger.data.model.Message
import com.example.messenger.data.repositories.MessagesRepository
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddMessagesListenerUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
){
    suspend fun addMessagesListener(existingMessagesPath: DatabaseReference): Flow<Message> =
        withContext(Dispatchers.IO) {
            messagesRepository.addMessagesListener(existingMessagesPath)
        }
}
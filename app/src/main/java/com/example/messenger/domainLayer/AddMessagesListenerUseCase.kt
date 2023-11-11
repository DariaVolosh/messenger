package com.example.messenger.domainLayer

import com.example.messenger.data.Message
import com.example.messenger.dataLayer.MessagesRepository
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddMessagesListenerUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
){
    fun addMessagesListener(existingMessagesPath: DatabaseReference): Flow<Message> =
        messagesRepository.addMessagesListener(existingMessagesPath)
}
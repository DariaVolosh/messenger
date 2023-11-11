package com.example.messenger.domainLayer

import com.example.messenger.data.Message
import com.example.messenger.dataLayer.MessagesRepository
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
){
    fun sendMessage(message: Message, conversationReference: DatabaseReference) {
        messagesRepository.sendMessage(message, conversationReference)
    }
}
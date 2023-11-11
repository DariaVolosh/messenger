package com.example.messenger.dataLayer

import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface MessagesRepository {
    fun fetchLastMessages(chats: List<User>, currentUserId: String): Deferred<List<Message>>
    fun sendMessage(message: Message, existingMessagesPath: DatabaseReference)
    fun addMessagesListener(existingMessagesPath: DatabaseReference): Flow<Message>
}

class FirebaseMessages @Inject constructor(
    private val chatsRepository: ChatsRepository,
): MessagesRepository {
    override fun fetchLastMessages(chats: List<User>, currentUserId: String): Deferred<List<Message>> =
        CoroutineScope(Dispatchers.IO).async {
            val lastMessages = mutableListOf<Message>()

            for (user in chats) {
                val conversationId = chatsRepository.getConversationReference(currentUserId, user.userId)
                val lastMessageSnapshot = conversationId.child("lastMessage").get().await()
                val message = lastMessageSnapshot.getValue(Message::class.java)
                message?.let { lastMessages.add(message) }
            }
            lastMessages
        }

    override fun sendMessage(message: Message, existingMessagesPath: DatabaseReference) {
        existingMessagesPath.child(message.messageId).setValue(message)
        existingMessagesPath.child("lastMessage").setValue(message)
    }

    override fun addMessagesListener(existingMessagesPath: DatabaseReference): Flow<Message> {
        val flow = MutableSharedFlow<Message>(replay = 1, onBufferOverflow = BufferOverflow.SUSPEND)

        existingMessagesPath.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let {message ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (snapshot.key != "lastMessage") {
                            flow.emit(message)
                            val lastMessage = existingMessagesPath.child("lastMessage").get().await()
                            if (lastMessage.getValue(Message::class.java) == message) {
                                flow.emit(Message())
                            }
                        } else {
                            flow.emit(Message())
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        return flow
    }
}
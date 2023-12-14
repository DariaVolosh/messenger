package com.example.messenger.data.repositories

import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

interface MessagesRepository {
    suspend fun fetchLastMessages(
        chats: List<User>,
        currentUserId: String,
        oldLastMessages: MutableList<Message>
    )
    fun sendMessage(message: Message, existingMessagesPath: DatabaseReference)
    fun getMessagesFlow(): Flow<Message>
    fun addMessagesListener(existingMessagesPath: DatabaseReference)
    fun removeMessagesListener(existingMessagesPath: DatabaseReference)
    fun getLastMessagesFlow(): Flow<List<Message>>
}

@Singleton
class FirebaseMessages @Inject constructor(
    private val chatsRepository: ChatsRepository,
): MessagesRepository {
    private val lastMessagesFlow = MutableSharedFlow<List<Message>>()
    private val messagesFlow = MutableSharedFlow<Message>()
    private lateinit var currentMessagesEventListener: ChildEventListener

    override suspend fun fetchLastMessages(
        chats: List<User>,
        currentUserId: String,
        oldLastMessages: MutableList<Message>
    ) {
        val mutex = Mutex()
        for (i in chats.indices) {
            val messagesRef = chatsRepository.getConversationReference(
                currentUserId, chats[i].userId
            )
            val newIndex = i
            messagesRef.child("lastMessage")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val message = snapshot.getValue(Message::class.java)
                            message?.let {
                                mutex.withLock {
                                    if (newIndex >= oldLastMessages.size) {
                                        while (oldLastMessages.size <= newIndex) {
                                            oldLastMessages.add(Message())
                                        }
                                        oldLastMessages[newIndex] = message
                                    } else {
                                        oldLastMessages[newIndex] = message
                                    }
                                    lastMessagesFlow.emit(oldLastMessages)
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun sendMessage(message: Message, existingMessagesPath: DatabaseReference) {
        existingMessagesPath.child(message.messageId).setValue(message)
        existingMessagesPath.child("lastMessage").setValue(message)
    }

    override fun getMessagesFlow(): MutableSharedFlow<Message> = messagesFlow
    override fun addMessagesListener(
        existingMessagesPath: DatabaseReference
    ) {
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (snapshot.key != "lastMessage") {
                        val message = snapshot.getValue(Message::class.java)

                        message?.let {
                            messagesFlow.emit(message)
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }

        existingMessagesPath.addChildEventListener(listener)
        currentMessagesEventListener = listener
    }

    override fun removeMessagesListener(existingMessagesPath: DatabaseReference) {
        existingMessagesPath.removeEventListener(currentMessagesEventListener)
    }

    override fun getLastMessagesFlow(): Flow<List<Message>> = lastMessagesFlow
}
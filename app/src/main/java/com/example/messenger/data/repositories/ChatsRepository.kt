package com.example.messenger.data.repositories

import com.example.messenger.data.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface ChatsRepository {
    suspend fun fetchChats(currentUId: String)
    fun getConversationReference(userId: String, friendId: String): DatabaseReference
    fun addChatToChatsList(currentUserId: String, friendId: String)

    fun getChatsFlow(): Flow<List<User>>
}


@Singleton
class FirebaseChats @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
): ChatsRepository {

    private val chatsFlow = MutableSharedFlow<List<User>>()
    override suspend fun fetchChats(currentUId: String) {
        val list = mutableListOf<User>()
        val mutex = Mutex()

        firebaseDatabase.getReference("users/$currentUId/chats").addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val ref =
                            firebaseDatabase.getReference("users/$currentUId/chats").get().await()
                        val expectedQuantity = ref.children.count()

                        val uId = snapshot.getValue(String::class.java)
                        val userSnapshot = firebaseDatabase.getReference("users/$uId").get().await()
                        val user = userSnapshot.getValue(User::class.java)

                        user?.let {
                            mutex.withLock {
                                list.add(it)
                                if (list.size == expectedQuantity) {
                                    chatsFlow.emit(list)
                                }
                            }
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
                }
            )
    }

    override fun getChatsFlow() = chatsFlow

    override fun getConversationReference(currentUserId: String, friendId: String): DatabaseReference {
        val sortedUserIds = listOf(currentUserId, friendId).sorted()
        return firebaseDatabase
            .getReference(
                "messages/${sortedUserIds[0]}_${sortedUserIds[1]}"
            )
    }

    override fun addChatToChatsList(currentUserId: String, friendId: String) {
        firebaseDatabase.getReference("users/$currentUserId").get()
            .addOnSuccessListener {dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)!!
                if (!user.chats.contains(friendId)) {
                    user.chats.add(friendId)
                    firebaseDatabase.getReference("users/$currentUserId").setValue(user)
                }
            }
        firebaseDatabase.getReference("users/$friendId").get()
            .addOnSuccessListener {dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)!!
                if (!user.chats.contains(currentUserId)) {
                    user.chats.add(currentUserId)
                    firebaseDatabase.getReference("users/$friendId").setValue(user)
                }
            }
    }
}
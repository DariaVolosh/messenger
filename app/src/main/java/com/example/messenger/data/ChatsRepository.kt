package com.example.messenger.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ChatsRepository {
    suspend fun fetchChats(currentUId: String): List<User>
    fun getConversationReference(userId: String, friendId: String): DatabaseReference
    fun addChatToChatsList(currentUserId: String, friendId: String)
}

class FirebaseChats @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
): ChatsRepository {
    override suspend fun fetchChats(currentUId: String): List<User> {
        val list = mutableListOf<User>()
        val dataSnapshot =
            firebaseDatabase.getReference("users/$currentUId/chats").get().await()

        for (snapshot in dataSnapshot.children) {
            val uId = snapshot.getValue(String::class.java)
            val userSnapshot = firebaseDatabase.getReference("users/$uId").get().await()
            val user = userSnapshot.getValue(User::class.java)
            user?.let { list.add(it) }
        }

        return list
    }

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
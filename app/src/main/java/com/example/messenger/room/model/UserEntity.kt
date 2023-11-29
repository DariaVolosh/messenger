package com.example.messenger.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val firebaseUserId: String,
    val login: String,
    val email: String,
    val fullName: String,
    val photoRef: String,

    val chats: List<ChatEntity>
)
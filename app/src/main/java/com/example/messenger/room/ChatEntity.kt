package com.example.messenger.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity (
    @PrimaryKey(autoGenerate = true)
    val chatId: Long = 0,
    val participants: List<UserEntity>,
    val lastMessage: String
)
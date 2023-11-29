package com.example.messenger.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.messenger.room.model.ChatEntity
import com.example.messenger.room.model.UserEntity

@Database(entities = [ChatEntity::class, UserEntity::class], version = 2)
@TypeConverters(UserListTypeConverter::class, ChatListTypeConverter::class)
abstract class UserAndChatDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDAO
    abstract fun userDao(): UserDAO
}
package com.example.messenger.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ChatEntity::class, UserEntity::class], version = 2)
@TypeConverters(UserListTypeConverter::class, ChatListTypeConverter::class)
abstract class UserAndChatDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDAO
    abstract fun userDao(): UserDAO
}
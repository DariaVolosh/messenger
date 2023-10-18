package com.example.messenger.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ChatEntity::class, UserEntity::class], version = 2)
@TypeConverters(UserListTypeConverter::class, ChatListTypeConverter::class)
abstract class UserAndChatDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDAO
    abstract fun userDao(): UserDAO

    companion object {
        private var INSTANCE: UserAndChatDatabase? = null

        fun getInstance(context: Context): UserAndChatDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    UserAndChatDatabase::class.java,
                    "userAndChatDatabase.db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
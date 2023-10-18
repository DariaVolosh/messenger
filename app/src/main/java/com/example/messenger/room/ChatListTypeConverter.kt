package com.example.messenger.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatListTypeConverter {
    @TypeConverter
    fun fromChatList(userList: List<ChatEntity>): String {
        val gson = Gson()
        return gson.toJson(userList)
    }

    @TypeConverter
    fun toChatList(userListString: String): List<ChatEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<ChatEntity>>() {}.type
        return gson.fromJson(userListString, type)
    }
}
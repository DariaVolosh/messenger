package com.example.messenger.room

import androidx.room.TypeConverter
import com.example.messenger.room.model.UserEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserListTypeConverter {
    @TypeConverter
    fun fromUserList(userList: List<UserEntity>): String {
        val gson = Gson()
        return gson.toJson(userList)
    }

    @TypeConverter
    fun toUserList(userListString: String): List<UserEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<UserEntity>>() {}.type
        return gson.fromJson(userListString, type)
    }
}
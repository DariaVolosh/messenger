package com.example.messenger.room

import com.example.messenger.room.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(private val userDAO: UserDAO) {

    fun insertUser(user: UserEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            userDAO.insertUser(user)
        }
    }

    fun getUserByFirebaseId(id: String): UserEntity =
        userDAO.getUserByFirebaseId(id)
}
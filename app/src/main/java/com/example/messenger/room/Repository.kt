package com.example.messenger.room

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(private val userDAO: UserDAO) {

    fun insertUser(user: UserEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            userDAO.insertUser(user)
        }
    }

    fun getUserByFirebaseId(id: String): Deferred<UserEntity?> =
        CoroutineScope(Dispatchers.IO).async {
            userDAO.getUserByFirebaseId(id)
        }
}
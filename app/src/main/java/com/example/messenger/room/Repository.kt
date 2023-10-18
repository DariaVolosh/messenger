package com.example.messenger.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    fun getUserByFirebaseId(id: String): LiveData<UserEntity> {
        val liveData = MutableLiveData<UserEntity>()
        CoroutineScope(Dispatchers.IO).launch {
            liveData.postValue(userDAO.getUserByFirebaseId(id))
        }
        return liveData
    }
}
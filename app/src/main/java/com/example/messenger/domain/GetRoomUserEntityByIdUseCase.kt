package com.example.messenger.domain

import com.example.messenger.data.repositories.RoomUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRoomUserEntityByIdUseCase @Inject constructor(
    private val roomUser: RoomUser
) {
    suspend fun getUserEntityById(id: String) = withContext(Dispatchers.IO) {
        roomUser.getUserEntityById(id)
    }
}
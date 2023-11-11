package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.RoomUser
import javax.inject.Inject

class GetRoomUserEntityByIdUseCase @Inject constructor(
    private val roomUser: RoomUser
) {
    fun getUserEntityById(id: String) = roomUser.getUserEntityById(id)
}
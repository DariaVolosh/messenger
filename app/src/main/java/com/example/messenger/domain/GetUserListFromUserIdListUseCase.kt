package com.example.messenger.domain

import com.example.messenger.presenter.mapper.UserIdListToUserListMapper
import javax.inject.Inject

class GetUserListFromUserIdListUseCase @Inject constructor(
    private val userIdListToUserList: UserIdListToUserListMapper
) {
    suspend fun userIdListToUserList(ids: List<String>) = userIdListToUserList.map(ids)
}
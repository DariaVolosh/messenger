package com.example.messenger.domain

import com.example.messenger.data.User
import com.example.messenger.presenter.mapper.Mapper
import com.example.messenger.presenter.mapper.UserIdListToUserListMapper
import javax.inject.Inject

class GetUserListFromUserIdListUseCase @Inject constructor(
    private val userIdListToUserList: Mapper<List<String>, List<User>>
) {
    suspend fun userIdListToUserList(ids: List<String>) = userIdListToUserList.map(ids)
}
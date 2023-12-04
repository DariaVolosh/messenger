package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.presenter.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserListFromUserIdListUseCase @Inject constructor(
    private val userIdListToUserList: Mapper<List<String>, List<User>>
) {
    suspend fun userIdListToUserList(ids: List<String>) =
        withContext(Dispatchers.IO) {
            userIdListToUserList.map(ids)
        }
}
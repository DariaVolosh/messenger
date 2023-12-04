package com.example.messenger.domain.user

import com.example.messenger.data.model.User
import com.example.messenger.presenter.friendsAndRequests.DataModel
import com.example.messenger.presenter.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetDataModelListFromUserListUseCase @Inject constructor(
    private val mapper: Mapper<List<User>, List<DataModel>>
) {
    suspend fun getDataModelListFromUserList(users: List<User>): List<DataModel> =
        withContext(Dispatchers.IO) {
            mapper.map(users)
        }
}
package com.example.messenger.domain

import com.example.messenger.data.User
import com.example.messenger.presenter.friendsAndRequests.DataModel
import com.example.messenger.presenter.mapper.Mapper
import javax.inject.Inject

class GetDataModelListFromUserListUseCase @Inject constructor(
    private val mapper: Mapper<List<User>, List<DataModel>>
) {
    suspend fun getDataModelListFromUserList(users: List<User>): List<DataModel> = mapper.map(users)
}
package com.example.messenger.domain

import com.example.messenger.data.User
import com.example.messenger.presenter.friendsAndRequests.DataModel
import com.example.messenger.presenter.mapper.UserListToDataModelListMapper
import javax.inject.Inject

class GetDataModelListFromUserListUseCase @Inject constructor(
    private val mapper: UserListToDataModelListMapper
) {
    suspend fun getDataModelListFromUserList(users: List<User>): List<DataModel> = mapper.map(users)
}
package com.example.messenger.presenter.mapper

import com.example.messenger.data.User
import com.example.messenger.presenter.friendsAndRequests.DataModel
import javax.inject.Inject

class UserToDataModelMapper @Inject constructor(): Mapper<User, DataModel> {
    fun map (from: User, friend: Boolean): DataModel {
        return if (friend) {
            DataModel.Friend(from.fullName, from.login, from.userId)
        } else {
            DataModel.FriendRequest(from.fullName, from.login, from.userId)
        }
    }

    override suspend fun map(from: User): DataModel {
        throw UnsupportedOperationException("This method requires an extra parameter.")
    }
}

class UserListToDataModelListMapper @Inject constructor(
    private val userMapper: UserToDataModelMapper,
    private val currentUser: User
): Mapper<List<@JvmSuppressWildcards User>, List<@JvmSuppressWildcards DataModel>> {

    override suspend fun map(from: List<User>): List<DataModel> {
        val dataModelObjects = mutableListOf<DataModel>()
        var firstFriendRequestFound = false
        for (user in from) {
            if (currentUser.receivedFriendRequests.contains(user.userId)) {
                if (!firstFriendRequestFound) {
                    firstFriendRequestFound = true
                    dataModelObjects.add(
                        DataModel.FriendsRequestsText(
                            "You have ${currentUser.receivedFriendRequests.size} requests"
                        )
                    )
                }
                dataModelObjects.add(userMapper.map(user, false))
            } else {
                dataModelObjects.add(userMapper.map(user, true))
                if (from.lastIndexOf(user) == from.size - 1) {
                    dataModelObjects.add(
                        DataModel.FriendsRequestsText(
                            "You have ${currentUser.receivedFriendRequests.size} requests"
                        )
                    )
                }
            }
        }

        return dataModelObjects
    }
}
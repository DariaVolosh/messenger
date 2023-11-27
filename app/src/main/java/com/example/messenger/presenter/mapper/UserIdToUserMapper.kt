package com.example.messenger.presenter.mapper

import com.example.messenger.data.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserIdToUserMapper @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
): Mapper<String, User> {
    override suspend fun map(from: String): User {
        val userSnapshot = firebaseDatabase.getReference("users/$from").get().await()
        return userSnapshot.getValue(User::class.java) ?: User()
    }
}

class UserIdListToUserListMapper @Inject constructor(
    private val userIdToUserMapper: UserIdToUserMapper
): Mapper<List<@JvmSuppressWildcards String>, List<@JvmSuppressWildcards User>> {
    override suspend fun map(from: List<String>): List<User> {
        return from.map {
                id -> userIdToUserMapper.map(id)
        }
    }
}
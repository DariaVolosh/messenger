package com.example.messenger.presenter.mapper

import android.net.Uri
import com.example.messenger.data.model.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserToPhotoUriMapper @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): Mapper<@JvmSuppressWildcards String, @JvmSuppressWildcards Uri> {
    override suspend fun map(from: String): Uri =
        firebaseStorage.getReference("avatars/$from").downloadUrl.await()
}

class UserListToPhotoUriListMapper @Inject constructor(
    private val userToPhotoUriMapper: UserToPhotoUriMapper
): Mapper<List<@JvmSuppressWildcards User>, List<@JvmSuppressWildcards Uri>> {
    override suspend fun map(from: List<User>): List<Uri> =
        from.map { user ->
            userToPhotoUriMapper.map(user.userId)
        }
}
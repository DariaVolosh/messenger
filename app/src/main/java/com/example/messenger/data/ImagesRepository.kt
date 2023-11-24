package com.example.messenger.data

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

interface ImagesRepository {
    suspend fun getImages(list: List<User>): List<Uri>
    suspend fun getMyImageUri(currentUserObject: User, internetAvailable: Boolean): Uri
    suspend fun uploadPhoto(photoUri: Uri, user: User): Boolean

    suspend fun getImageById(id: String): Uri
}

class FirebaseImages @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val roomUserRepository: RoomUser
): ImagesRepository {
    override suspend fun getImages(list: List<User>): List<Uri> {
        val deferredUris = list.map { user ->
            val uri = firebaseStorage.getReference("avatars/${user.userId}").downloadUrl.await()
            uri
        }

        return deferredUris
    }

    override suspend fun getMyImageUri(currentUserObject: User, internetAvailable: Boolean): Uri {
        val uri: Uri = if (internetAvailable) {
            firebaseStorage
                .getReference("avatars/${currentUserObject.userId}").downloadUrl.await()
        } else {
            val roomUser =
                roomUserRepository.getUserEntityById(currentUserObject.userId)
            if (roomUser == null) Uri.parse("")
            else Uri.parse(roomUser.photoRef)
        }

        return uri
    }

    override suspend fun uploadPhoto(photoUri: Uri, user: User): Boolean {
        val photoUploaded: CompletableDeferred<Boolean> = CompletableDeferred()
        firebaseStorage.getReference("avatars/${user.userId}")
            .putFile(photoUri).addOnSuccessListener {
                val localFile = File.createTempFile(user.userId, "jpeg")
                firebaseStorage
                    .getReference("avatars/${user.userId}")
                    .getFile(localFile)
                    .addOnSuccessListener {
                        photoUploaded.complete(true)
                        roomUserRepository.insertUser(user, Uri.parse(localFile.absolutePath))
                }.addOnFailureListener {exception ->
                    if (exception is StorageException) {
                        Log.e("FirebaseStorage", "Error code: ${exception.errorCode}")
                    }
                }
            }.addOnFailureListener {
                photoUploaded.complete(false)
            }

        return photoUploaded.await()
    }

    override suspend fun getImageById(id: String): Uri {
        return firebaseStorage.getReference("avatars/$id").downloadUrl.await()
    }
}
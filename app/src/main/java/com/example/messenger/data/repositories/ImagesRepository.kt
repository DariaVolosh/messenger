package com.example.messenger.data.repositories

import android.net.Uri
import android.util.Log
import com.example.messenger.data.model.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ImagesRepository {
    suspend fun getMyImageUri(currentUserObject: User): Uri
    suspend fun uploadPhoto(photoUri: Uri, user: User): Boolean

}

class FirebaseImages @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): ImagesRepository {
    override suspend fun getMyImageUri(currentUserObject: User): Uri {
        return firebaseStorage
            .getReference("avatars/${currentUserObject.userId}").downloadUrl.await()
    }

    override suspend fun uploadPhoto(photoUri: Uri, user: User): Boolean {
        val photoUploaded: CompletableDeferred<Boolean> = CompletableDeferred()

        firebaseStorage.getReference("avatars/${user.userId}")
            .putFile(photoUri)
            .addOnSuccessListener {
                    photoUploaded.complete(true)
            }.addOnFailureListener {exception ->
                photoUploaded.complete(false)

                if (exception is StorageException) {
                    Log.e("FirebaseStorage", "Error code: ${exception.errorCode}")
                }
            }

        return photoUploaded.await()
    }
}
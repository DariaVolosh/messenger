package com.example.messenger.dataLayer

import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.example.messenger.R
import com.example.messenger.data.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

interface ImagesRepository {
    fun getImages(list: List<User>): Deferred<List<Uri>>
    fun getMyImageUri(currentUserObject: User, internetAvailable: Boolean): Deferred<Uri>
    fun uploadPhoto(photoUri: Uri, user: User)

    fun getImageById(id: String): Deferred<Uri>
}

class FirebaseImages @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val roomUserRepository: RoomUser,
    private val userRepository: UserRepository,
    private val navController: NavController
): ImagesRepository {
    override fun getImages(list: List<User>): Deferred<List<Uri>> =
        CoroutineScope(Dispatchers.IO).async {
            val deferredUris = list.map { user ->
                val uri = firebaseStorage.getReference("avatars/${user.userId}").downloadUrl.await()
                uri
            }

            deferredUris
        }

    override fun getMyImageUri(currentUserObject: User, internetAvailable: Boolean): Deferred<Uri> =
        CoroutineScope(Dispatchers.IO).async {
            val uri: Uri = if (internetAvailable) {
                firebaseStorage
                    .getReference("avatars/${currentUserObject.userId}").downloadUrl.await()
            } else {
                val roomUser = roomUserRepository.getUserEntityById(currentUserObject.userId).await()
                if (roomUser == null) Uri.parse("")
                else Uri.parse(roomUser.photoRef)
            }
            uri
        }

    override fun uploadPhoto(photoUri: Uri, user: User) {
        val currentUId = userRepository.getCurrentUserId()
        firebaseStorage.getReference("avatars/$currentUId")
            .putFile(photoUri).addOnSuccessListener {
                navController.navigate(R.id.chats_fragment)

                val localFile = File.createTempFile(currentUId, "jpeg")
                firebaseStorage
                    .getReference("avatars/$currentUId}")
                    .getFile(localFile)
                    .addOnSuccessListener {
                        roomUserRepository.insertUser(user, Uri.parse(localFile.absolutePath))
                }.addOnFailureListener {exception ->
                    if (exception is StorageException) {
                        Log.e("FirebaseStorage", "Error code: ${exception.errorCode}")
                    }
                }
            }

    }

    override fun getImageById(id: String) = CoroutineScope(Dispatchers.IO).async {
        firebaseStorage.getReference("avatars/$id").downloadUrl.await()
    }
}
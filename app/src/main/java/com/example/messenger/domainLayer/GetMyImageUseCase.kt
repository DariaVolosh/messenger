package com.example.messenger.domainLayer

import android.net.Uri
import com.example.messenger.data.User
import com.example.messenger.dataLayer.FirebaseImages
import com.example.messenger.dataLayer.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetMyImageUseCase @Inject constructor(
    private val imagesRepository: FirebaseImages,
    private val userRepository: UserRepository,
    private val getCurrentUserObjectUseCase: GetCurrentUserObjectUseCase
) {
    fun getMyImage(internetAvailable: Boolean): Deferred<Uri> =
        CoroutineScope(Dispatchers.IO).async {
            val uriDeferred = CompletableDeferred<Uri>()

            val currentUser: User? = if (internetAvailable) {
                getCurrentUserObjectUseCase.currentUser?.await()
            } else {
                val userId = userRepository.getCurrentUserId()
                userId?.let {
                    User("", "", "",
                        it,
                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf()
                    )
                }
            }

            val result = currentUser?.let {
                imagesRepository.getMyImageUri(it, internetAvailable).await()
            }
            if (result == null) uriDeferred.complete(Uri.parse(""))
            else uriDeferred.complete(result)

            uriDeferred.await()
        }
}
package com.example.messenger.dataLayer

import android.net.Uri
import androidx.navigation.NavController
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.room.Repository
import com.example.messenger.room.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.lang.IllegalArgumentException
import javax.inject.Inject

interface UserRepository {
    fun getUserById(id: String): Deferred<User>
    fun getUsersByLogin(loginQuery: String, currentUserObject: User): Deferred<List<User>>
    fun getCurrentUserId(): String?
    fun updateUser(user: User)
    fun signInUser(email: String, password: String)
    fun signUpUser(
        user: User,
        password: String,
        photoUri: Uri,
        uploadPhotoCallback: () -> Unit,
        updateCurrentUserObjectCallback: () -> Unit)
    fun insertUser(user: User, photoUri: Uri)
    fun getUsersByIds(ids: List<String>): Deferred<List<User>>
}

class FirebaseUser @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
    private val navController: NavController
) : UserRepository {
    override fun getUserById(id: String): Deferred<User> =
        CoroutineScope(Dispatchers.IO).async {
            val userSnapshot = firebaseDatabase.getReference("users/$id").get().await()
            val user = userSnapshot.getValue(User::class.java)
                ?: throw IllegalArgumentException("User not found")
            user
        }

    override fun getUsersByLogin(
        loginQuery: String,
        currentUserObject: User
    ): Deferred<List<User>> =
        CoroutineScope(Dispatchers.IO).async {
            val foundUsers = mutableListOf<User>()
            if (loginQuery.isNotEmpty()) {
                val usersSnapshot = firebaseDatabase.getReference("users").get().await()
                for (snap in usersSnapshot.children) {
                    val user = snap.getValue(User::class.java)

                    if (user != null &&
                        user.login.startsWith(loginQuery) &&
                        !user.login.startsWith(currentUserObject.login) &&
                        !currentUserObject.friends.contains(user.userId)
                    ) {
                        foundUsers.add(user)
                    }
                }
                foundUsers
            } else {
                listOf()
            }
        }

    override fun getCurrentUserId() = firebaseAuth.currentUser?.uid

    override fun updateUser(user: User) {
        firebaseDatabase.getReference("users/${user.userId}").setValue(user)
    }

    override fun signInUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            navController.navigate(R.id.chats_fragment)
        }
    }

    override fun signUpUser(
        user: User,
        password: String,
        photoUri: Uri,
        uploadPhotoCallback: () -> Unit,
        updateCurrentUserObjectCallback: () -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
            val currentUId = getCurrentUserId()
            currentUId?.let { user.userId = currentUId }
            updateCurrentUserObjectCallback()
            updateUser(user)
            uploadPhotoCallback()
        }
    }

    override fun insertUser(user: User, photoUri: Uri) {
        updateUser(user)
    }

    override fun getUsersByIds(ids: List<String>) = CoroutineScope(Dispatchers.IO).async {
        ids.map { id ->
            val userSnapshot = firebaseDatabase.getReference("users/$id").get().await()
            userSnapshot.getValue(User::class.java) ?: User()
        }
    }

    fun getFirebaseUser(): FirebaseUser? = firebaseAuth.currentUser
}

class RoomUser @Inject constructor(
    private val roomRepository: Repository,
    private val userRepository: UserRepository
): UserRepository {
    fun getUserEntityById(id: String): Deferred<UserEntity?> =
        CoroutineScope(Dispatchers.IO).async {
            val deferredUser = roomRepository.getUserByFirebaseId(id).await()
            deferredUser
        }

    override fun getUserById(id: String): Deferred<User> = CompletableDeferred()

    override fun getUsersByLogin(
        loginQuery: String,
        currentUserObject: User
    ): Deferred<List<User>> = CompletableDeferred()

    override fun getCurrentUserId(): String = ""

    override fun updateUser(user: User) {}
    override fun signInUser(email: String, password: String) {}
    override fun signUpUser(
        user: User,
        password: String,
        photoUri: Uri,
        uploadPhotoCallback: () -> Unit,
        updateCurrentUserObjectCallback: () -> Unit
    ) {}

    override fun insertUser(user: User, photoUri: Uri) {
        userRepository.getCurrentUserId()?.let {id ->
            val roomUser = UserEntity(
                firebaseUserId = id,
                login = user.login,
                email = user.email,
                photoRef = photoUri.toString(),
                fullName = user.fullName,
                chats = listOf()
            )

            roomRepository.insertUser(roomUser)
        }
    }

    override fun getUsersByIds(ids: List<String>): Deferred<List<User>> = CompletableDeferred()
}
package com.example.messenger.data.repositories

import android.net.Uri
import com.example.messenger.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface UserRepository {
    suspend fun getUserById(id: String): User
    suspend fun getUsersByLogin(loginQuery: String, currentUserObject: User): List<User>
    fun getCurrentUserId(): String?
    fun updateUser(user: User)
    suspend fun signInUser(email: String, password: String): Boolean
    suspend fun signUpUser(
        user: User,
        password: String,
        photoUri: Uri): Boolean
    fun insertUser(user: User, photoUri: Uri)
    fun signOut()

    fun getFirebaseUser(): FirebaseUser?
}

class FirebaseUser @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    override suspend fun getUserById(id: String): User {
        val currentUser = CompletableDeferred<User>()
        val userSnapshot = firebaseDatabase.getReference("users/$id").get().await()

        currentUser.complete(userSnapshot.getValue(User::class.java) ?:
        throw IllegalArgumentException("User not found"))

        return currentUser.await()
    }

    override suspend fun getUsersByLogin(
        loginQuery: String,
        currentUserObject: User
    ): List<User> {
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

        return foundUsers
    }

    override fun getCurrentUserId() = firebaseAuth.currentUser?.uid

    override fun updateUser(user: User) {
        firebaseDatabase.getReference("users/${user.userId}").setValue(user)
    }

    override suspend fun signInUser(email: String, password: String): Boolean {
        val signedIn = CompletableDeferred<Boolean>()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            signedIn.complete(true)
        }.addOnFailureListener {
            signedIn.complete(false)
        }

        return signedIn.await()
    }

    override suspend fun signUpUser(
        user: User,
        password: String,
        photoUri: Uri
    ): Boolean {
        val userCreated: CompletableDeferred<Boolean> = CompletableDeferred()

        firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
            val currentUId = getCurrentUserId()
            currentUId?.let { user.userId = currentUId }
            userCreated.complete(true)
            updateUser(user)
        }.addOnFailureListener {
            userCreated.complete(false)
        }

        return userCreated.await()
    }

    override fun insertUser(user: User, photoUri: Uri) {
        updateUser(user)
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getFirebaseUser(): FirebaseUser? = firebaseAuth.currentUser
}
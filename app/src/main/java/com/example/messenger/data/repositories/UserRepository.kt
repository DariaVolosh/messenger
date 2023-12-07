package com.example.messenger.data.repositories

import android.net.Uri
import com.example.messenger.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

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

    fun setOnlineStatus(online: Boolean, id: String)

    fun getOnlineUserStatusFlowList(list: List<User>): List<Flow<Boolean>>

    fun emitOnlineValues(list: List<Flow<Boolean>>, users: List<User>)

    fun getOnlineFlowById(id: String): Flow<Boolean>
}

@Singleton
class FirebaseUser @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    override suspend fun getUserById(id: String): User {
        val userSnapshot = firebaseDatabase.getReference("users/$id").get().await()

        return userSnapshot.getValue(User::class.java)
            ?: throw IllegalArgumentException("User not found")
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
            getCurrentUserId()?.let { id ->
                setOnlineStatus(true, id)
            }
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

    override fun setOnlineStatus(online: Boolean, id: String) {
        firebaseDatabase.getReference("users/$id/online").setValue(online)
    }

    val onlineStatus = mutableMapOf<String, MutableSharedFlow<Boolean>>()
    override fun getOnlineUserStatusFlowList(list: List<User>): List<MutableSharedFlow<Boolean>> =
        list.map {
            var flow = MutableSharedFlow<Boolean>()
            flow
        }

    override fun emitOnlineValues(flows: List<Flow<Boolean>>, users: List<User>) {
        for (i in users.indices) {
            if (onlineStatus[users[i].userId] == null) {
                firebaseDatabase.getReference("users/${users[i].userId}/online")
                    .addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val onlineSnapshot = snapshot.getValue(Boolean::class.java)
                                onlineStatus[users[i].userId] = flows[i] as MutableSharedFlow<Boolean>
                                onlineStatus[users[i].userId]?.emit(onlineSnapshot ?: false)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else {
                firebaseDatabase.getReference("users/${users[i].userId}/online").get()
                    .addOnSuccessListener { snapshot ->
                        CoroutineScope(Dispatchers.IO).launch {
                            onlineStatus[users[i].userId]?.emit(
                                snapshot.getValue(Boolean::class.java) ?: false
                            )
                        }
                    }
            }
        }
    }

    override fun getOnlineFlowById(id: String) = onlineStatus[id] as Flow<Boolean>
}
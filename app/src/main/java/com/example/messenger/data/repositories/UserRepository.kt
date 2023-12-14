package com.example.messenger.data.repositories

import android.net.Uri
import com.example.messenger.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    suspend fun getOnlineStatus(id: String): Boolean

    fun getOnlineStatusChatsFlow(): Flow<List<Boolean>>
    fun getFriendRequestsListFlow(): Flow<List<User>>

    fun emitChatsOnlineValues(users: List<User>, oldOnlineStatuses: MutableList<Boolean>)
    fun emitMessagesOnlineStatus(userId: String)

    fun emitFriendRequests()
    fun getOnlineStatusMessagesFlow(): Flow<Boolean>
}

@Singleton
class FirebaseUser @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    private val chatsOnlineStatusFlow = MutableSharedFlow<List<Boolean>>()
    private val messagesOnlineStatusFlow = MutableSharedFlow<Boolean>()
    private val friendRequestsFlow = MutableSharedFlow<List<User>>()
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
                    !currentUserObject.friends.contains(user.userId) &&
                    !user.receivedFriendRequests.contains(currentUserObject.userId)
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

    override suspend fun getOnlineStatus(id: String): Boolean {
        val ref = firebaseDatabase.getReference("users/$id/online").get().await()
        return ref.getValue(Boolean::class.java) ?: false
    }

    override fun emitChatsOnlineValues(
        users: List<User>,
        oldOnlineStatuses: MutableList<Boolean>
    ) {
        val mutex = Mutex()
        for (i in users.indices) {
            val newIndex = i
            firebaseDatabase.getReference("users/${users[i].userId}/online")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val onlineSnapshot = snapshot.getValue(Boolean::class.java)
                            onlineSnapshot?.let { online ->
                                mutex.withLock {
                                    if (newIndex >= oldOnlineStatuses.size) {
                                        while (oldOnlineStatuses.size <= newIndex) {
                                            oldOnlineStatuses.add(false)
                                        }

                                        oldOnlineStatuses[newIndex] = online

                                    } else {
                                        oldOnlineStatuses[newIndex] = online
                                    }

                                    chatsOnlineStatusFlow.emit(oldOnlineStatuses)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun getOnlineStatusChatsFlow(): Flow<List<Boolean>> = chatsOnlineStatusFlow
    override fun getFriendRequestsListFlow(): Flow<List<User>> = friendRequestsFlow

    override fun emitMessagesOnlineStatus(userId: String) {
        val mutex = Mutex()
        var onlineStatus: Boolean
        firebaseDatabase.getReference("users/$userId/online")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val onlineSnapshot = snapshot.getValue(Boolean::class.java)
                        onlineSnapshot?.let { online ->
                            mutex.withLock {
                                onlineStatus = online

                                messagesOnlineStatusFlow.emit(onlineStatus)

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


    }

    override fun emitFriendRequests() {
        val list = mutableListOf<User>()
        val mutex = Mutex()

        firebaseDatabase.getReference("users/${getCurrentUserId()}/receivedFriendRequests")
            .addChildEventListener(
                object: ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val expectedQuantity =
                                firebaseDatabase.getReference(
                                    "users/${getCurrentUserId()}/receivedFriendRequests"
                                ).get().await().children.count()

                            val uId = snapshot.getValue(String::class.java)
                            uId?.let { id ->
                                val request = getUserById(id)

                                mutex.withLock {
                                    list.add(request)
                                    if (list.size == expectedQuantity) {
                                        friendRequestsFlow.emit(list)
                                    }
                                }
                            }
                        }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?, ) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val id = snapshot.getValue(String::class.java)
                            id?.let {
                                val user = getUserById(it)

                                mutex.withLock {
                                    list.remove(user)
                                    friendRequestsFlow.emit(list)
                                }
                            }
                        }
                    }
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                }
            )
    }

    override fun getOnlineStatusMessagesFlow(): Flow<Boolean> = messagesOnlineStatusFlow
}
package com.example.messenger

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.messenger.chats.ChatsAdapter
import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.example.messenger.room.Repository
import com.example.messenger.room.UserEntity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


class Model(val app: MyApp) {
    private val roomRepository = Repository(app.applicationContext)

    init {
        getCurrentUserObject()
    }

    // use live data to prevent usage of this variable if it is not initialized
    // and observe it to react only if it has a value
    var currentUserObject = MutableLiveData<User>()

    private fun getCurrentUserObject() {
        getCurrentUserUId()?.let {
            app.database.getReference("users/$it").get()
                .addOnSuccessListener { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)!!
                    currentUserObject.postValue(user)
                }
        }
    }

    fun getUserObjectById(id: String, friend: MutableLiveData<User>) {
        app.database.getReference("users/$id").get().addOnSuccessListener { userDataSnapshot ->
            val user = userDataSnapshot.getValue(User::class.java)!!
            friend.postValue(user)
        }
    }

    fun searchUserByLogin(loginQuery: String, liveData: MutableLiveData<List<User>>) {
        val foundUsers = mutableListOf<User>()

        if (loginQuery.isNotEmpty()) {
            app.database.getReference("users").get().addOnSuccessListener { usersSnapshot ->
                for (snap in usersSnapshot.children) {
                    val user = snap.getValue(User::class.java)

                    if (user != null &&
                        user.login.startsWith(loginQuery) &&
                        !user.login.startsWith(currentUserObject.value!!.login) &&
                        !currentUserObject.value!!.friends.contains(user.userId)
                    ) {
                        foundUsers.add(user)
                    }
                }
                liveData.postValue(foundUsers)
            }
        } else liveData.postValue(mutableListOf())
    }

    fun getCurrentUserUId() = app.auth.currentUser?.uid

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            app.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)
        var result = false
        if (actNw != null) {
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        return result
    }

    fun getCurrentRoomUser(): LiveData<UserEntity> =
        roomRepository.getUserByFirebaseId(getCurrentUserUId()!!)

    fun downloadMyMainPhoto(
        photoUriLiveData: MutableLiveData<Uri>,
        photoBitmapLiveData: MutableLiveData<Bitmap>,
        lifecycleOwner: LifecycleOwner,
    ) {
        if (isInternetAvailable()) {
            app.storage.getReference("avatars/${getCurrentUserUId()}").downloadUrl
                .addOnSuccessListener { photoUri ->
                    photoUriLiveData.postValue(photoUri)
                }
        } else {
            val roomUser = getCurrentRoomUser()
            roomUser.observe(lifecycleOwner) {userEntity ->
                val localFile = File(userEntity.photoRef)
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                photoBitmapLiveData.postValue(bitmap)
            }
        }
    }

    fun downloadFriendMainPhoto(photoUriLiveData: MutableLiveData<Uri>, id: String) {
        app.storage.getReference("avatars/$id").downloadUrl
            .addOnSuccessListener { photoUri ->
                photoUriLiveData.postValue(photoUri)
            }
    }

    fun downloadImages(list: List<User>, photoUrisLiveData: MutableLiveData<List<Uri>>) {
        val scope = CoroutineScope(Dispatchers.IO)
        val deferredUris = list.map { user ->
            scope.async {
                val uri = app.storage.getReference("avatars/${user.userId}").downloadUrl.await()
                uri
            }
        }

        scope.launch {
            val uris = deferredUris.awaitAll()
            photoUrisLiveData.postValue(uris)
        }
    }

    fun updateUser(updatedUser: User) {
        app.database.getReference("users/${updatedUser.userId}").setValue(updatedUser)
    }

    fun listenForNewChat(chats: MutableLiveData<MutableList<User>>) {
        val newList = chats.value ?: mutableListOf()
        var count = 0
        app.database.getReference("users/${getCurrentUserUId()}/chats")
            .addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        app.database.getReference("users/${getCurrentUserUId()}/chats").get()
                            .addOnSuccessListener { chatsSnapshot ->
                                count = chatsSnapshot.children.count()
                            }

                        val uId = snapshot.getValue(String::class.java)
                        app.database.getReference("users/$uId").get()
                            .addOnSuccessListener {userSnapshot ->
                                val user = userSnapshot.getValue(User::class.java)!!
                                newList.add(user)
                                if (newList.size == count) {
                                    chats.postValue(newList)
                                }
                        }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                }
            )
    }

    private fun getConversationId(currentUserUId: String, friendId: String): String {
        val sortedUserIds = listOf(currentUserUId, friendId).sorted()
        return "${sortedUserIds[0]}_${sortedUserIds[1]}"
    }

    fun listenForNewMessage(
        friendId: String,
        holder: ChatsAdapter.ViewHolder,
    ) {
        val conversationId = getCurrentUserUId()?.let { getConversationId(it, friendId) }
        app.database.getReference("messages/$conversationId/lastMessage").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageData = snapshot.getValue(Message::class.java)!!
                    holder.message.text = messageData.text
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    fun addChatToChatsList(friendId: String) {
        app.database.getReference("users/${getCurrentUserUId()}").get()
            .addOnSuccessListener {dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)!!
                if (!user.chats.contains(friendId)) {
                    user.chats.add(friendId)
                    app.database.getReference("users/${getCurrentUserUId()}").setValue(user)
                }
            }
        app.database.getReference("users/$friendId").get()
            .addOnSuccessListener {dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)!!
                if (!user.chats.contains(getCurrentUserUId())) {
                    getCurrentUserUId()?.let { user.chats.add(it) }
                    app.database.getReference("users/$friendId").setValue(user)
                }
            }
    }

    fun getExistingMessagesPath(friendId: String, path: MutableLiveData<DatabaseReference>) {
        path.postValue(
            app.database
                .getReference(
                    "messages/${getCurrentUserUId()?.let { getConversationId(it, friendId) }}"
                )
        )
    }

    fun addMessagesListener(
        existingMessagesPath: DatabaseReference,
        messagesLiveData: MutableLiveData<MutableList<Message>>,
    ) {
        val messagesList = messagesLiveData.value ?: mutableListOf()
        var count: Int
        existingMessagesPath.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                existingMessagesPath.get().addOnSuccessListener { innerSnapshot ->
                    count = innerSnapshot.children.count() - 1

                    if (snapshot.key != "lastMessage") {
                        val addedMessage = snapshot.getValue(Message::class.java)!!
                        messagesList.add(addedMessage)
                    }

                    if (count == messagesList.size) {
                        messagesLiveData.postValue(messagesList)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendMessage(message: Message, messageId: String, existingMessagesPath: DatabaseReference) {
        existingMessagesPath.child(messageId).setValue(message)
        existingMessagesPath.child("lastMessage").setValue(message)
    }

    fun getUsersFromUId(list: List<String>, userListLiveData: MutableLiveData<MutableList<User>>) {
        val scope = CoroutineScope(Dispatchers.IO)
        val deferredUsers = list.map { id ->
            scope.async {
                val userSnapshot = app.database.getReference("users/$id").get().await()
                userSnapshot.getValue(User::class.java)!!
            }
        }

        scope.launch {
            val users = deferredUsers.awaitAll()
            userListLiveData.postValue(users.toMutableList())
        }
    }

    fun listenForNewFriendRequests(friendRequestsLiveData: MutableLiveData<MutableList<String>>) {
        app.database.getReference("users/${getCurrentUserUId()}/receivedFriendRequests")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    for (request in snapshot.children) {
                        list.add(request.getValue(String::class.java)!!)

                        if (list.size == snapshot.children.count()) {
                            friendRequestsLiveData.postValue(list)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun uploadPhoto(photoUri: Uri,
                            navController: NavController,
                            login: String,
                            email: String,
                            fullName: String) {
        app.storage.getReference("avatars/${getCurrentUserUId()}")
            .putFile(photoUri).addOnSuccessListener {
                navController.navigate(R.id.chats_fragment)
                insertUserInRoom(
                    login,
                    email,
                    fullName
                )
            }
    }

    private fun insertUserInRoom(login: String, email: String, fullName: String) {
        saveImageLocally {
            val roomUser = UserEntity(
                firebaseUserId = getCurrentUserUId()!!,
                login = login,
                email = email,
                photoRef = it,
                fullName = fullName,
                chats = listOf()
            )
            roomRepository.insertUser(roomUser)
        }
    }

    private fun saveImageLocally(
        callback: (String) -> Unit,
    ) {
        val localFile = File.createTempFile(getCurrentUserUId()!!, "jpeg")
        app.storage.getReference("avatars/${getCurrentUserUId()}").getFile(localFile).addOnSuccessListener {
            callback(localFile.absolutePath)
        }.addOnFailureListener {exception ->
            if (exception is StorageException) {
                Log.e("FirebaseStorage", "Error code: ${exception.errorCode}")
            }
        }
    }

    fun createUser(
        email: String, login: String, fullName: String, password: String, photoUri: Uri,
        navController: NavController,
    ) {
        app.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val user = User(
                fullName, email, login, getCurrentUserUId()!!,
                mutableListOf(), mutableListOf(), mutableListOf()
            )
            updateUser(user)
            uploadPhoto(photoUri, navController, login, email, fullName)
        }
    }

    fun signInUser(email: String, password: String, navController: NavController) {
        app.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            navController.navigate(R.id.chats_fragment)
        }
    }
}
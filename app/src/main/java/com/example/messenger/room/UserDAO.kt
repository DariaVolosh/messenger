package com.example.messenger.room
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.messenger.room.model.UserEntity

@Dao
interface UserDAO {
    @Insert
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE firebaseUserId = :firebaseUserId")
    fun getUserByFirebaseId(firebaseUserId: String): UserEntity
}
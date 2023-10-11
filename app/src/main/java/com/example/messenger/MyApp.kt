package com.example.messenger

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MyApp : Application() {
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage

    override fun onCreate() {
        super.onCreate()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
    }
}
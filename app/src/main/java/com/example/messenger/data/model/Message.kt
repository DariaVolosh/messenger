package com.example.messenger.data.model

import androidx.annotation.Keep

@Keep
data class Message(
    val timestamp: Long,
    val text: String,
    val from: String,
    val to: String,
    val messageId: String) {
    constructor() : this(0, "", "", "", "")
}
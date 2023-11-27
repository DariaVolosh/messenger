package com.example.messenger.presenter.mapper
interface Mapper<From, To> {
    suspend fun map(from: From): To
}
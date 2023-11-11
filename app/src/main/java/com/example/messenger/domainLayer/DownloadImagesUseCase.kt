package com.example.messenger.domainLayer

import com.example.messenger.data.User
import com.example.messenger.dataLayer.ImagesRepository
import javax.inject.Inject

class DownloadImagesUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    fun getImages(list: List<User>) = imagesRepository.getImages(list)
}
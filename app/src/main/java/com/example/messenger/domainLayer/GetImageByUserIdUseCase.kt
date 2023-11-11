package com.example.messenger.domainLayer

import com.example.messenger.dataLayer.ImagesRepository
import javax.inject.Inject

class GetImageByUserIdUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    fun getImageById(id: String) = imagesRepository.getImageById(id)
}
package com.example.messenger.di.modules

import com.example.messenger.data.GlideImageLoader
import com.example.messenger.data.ImageLoader
import dagger.Binds
import dagger.Module

@Module
abstract class ImageLoaderModule {
    @Binds
    abstract fun bindImageLoader(glideImageLoader: GlideImageLoader): ImageLoader
}
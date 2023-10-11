package com.example.messenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory<T: ViewModel>(private val app: MyApp,
                                     private val viewModelClass: Class<T>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelClass.getDeclaredConstructor(app::class.java).newInstance(app) as T
    }
}
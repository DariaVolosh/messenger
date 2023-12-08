package com.example.messenger.di.modules

import android.net.Uri
import com.example.messenger.data.model.User
import com.example.messenger.presenter.mapper.Mapper
import com.example.messenger.presenter.mapper.UserIdListToUserListMapper
import com.example.messenger.presenter.mapper.UserListToDataModelListMapper
import com.example.messenger.presenter.mapper.UserListToPhotoUriListMapper
import com.example.messenger.presenter.mapper.UserToPhotoUriMapper
import dagger.Binds
import dagger.Module

@Module
abstract class MapperModule {
    @Binds
    abstract fun bindUserListToDataModelListMapper(mapper: UserListToDataModelListMapper):
            Mapper<List<User>, List<DataModel>>

    @Binds
    abstract fun bindUserIdListToUserListMapper(mapper: UserIdListToUserListMapper):
            Mapper<List<String>, List<User>>

    @Binds
    abstract fun bindUserListToPhotoUriListMapper(mapper: UserListToPhotoUriListMapper):
            Mapper<List<User>, List<Uri>>

    @Binds
    abstract fun bindUserToPhotoUriMapper(mapper: UserToPhotoUriMapper):
            Mapper<String, Uri>
}
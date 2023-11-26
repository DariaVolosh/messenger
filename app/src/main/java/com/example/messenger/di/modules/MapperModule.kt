package com.example.messenger.di.modules

import com.example.messenger.data.User
import com.example.messenger.presenter.friendsAndRequests.DataModel
import com.example.messenger.presenter.mapper.Mapper
import com.example.messenger.presenter.mapper.UserIdListToUserListMapper
import com.example.messenger.presenter.mapper.UserListToDataModelListMapper
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
}
package com.example.messenger.presenter.addFriend

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.messenger.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriend(
    viewModel: AddFriendViewModel
) {
    var login by remember { mutableStateOf("") }
    val currentUserId by viewModel.currentUserId.observeAsState()
    val foundUsers by viewModel.foundUsers.observeAsState()
    val images by viewModel.images.observeAsState()

    if (currentUserId == null) {
        viewModel.getCurrentUserId()
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TopAppBar(
            modifier = Modifier.height(75.dp),
            navigationIcon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_navigation_icon_description),
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = stringResource(id = R.string.add_friend_toolbar_title),
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
        )

        TextField(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth(),
            value = login,
            onValueChange = {
                login = it
                viewModel.searchUserByLogin(it)},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.search_icon_title),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )

        LazyColumn(
            modifier = Modifier.padding(top = 15.dp)
        ) {
            foundUsers?.let { users ->
                if (images != null && images!!.isNotEmpty()) {
                    itemsIndexed(users) {index, user ->
                        FoundUser(
                            user = user,
                            photoUri = images?.get(index) ?: Uri.parse("")
                        ) {
                            currentUserId?.let { id ->
                                user.receivedFriendRequests += id
                                viewModel.updateUser(user)
                            }
                        }
                    }
                }
            }
        }
    }
}
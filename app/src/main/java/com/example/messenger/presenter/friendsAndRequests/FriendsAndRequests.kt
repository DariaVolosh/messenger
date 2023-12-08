package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.messenger.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsAndRequestsScreen(
    friendsViewModel: FriendsViewModel
) {
    val images by friendsViewModel.images.observeAsState()
    val friend by friendsViewModel.friend.observeAsState()
    var initialized by rememberSaveable { mutableStateOf(false) }


    if (!initialized) {
        friendsViewModel.getCurrentUserObject()
        initialized = true
    }

    LaunchedEffect(friend) {
        friendsViewModel.currentUser.value?.let { currUser ->
            friend?.let { friendUser ->
                friendUser.friends += currUser.userId
                friendsViewModel.updateUser(friendUser)
                friendsViewModel.getCurrentUserObject()
            }
        }
    }

    Column {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_navigation_icon_description)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.friends_toolbar_title),
                    style = MaterialTheme.typography.displayMedium
                )
            }
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(vertical = 15.dp)
        ) {
            if (
                friendsViewModel.friendsList.value != null
                && friendsViewModel.friendsList.value?.size != 0
                && images != null
                && images?.size != 0
                ) {
                friendsViewModel.friendsList.value?.let {friends ->
                    itemsIndexed(friends) {index, user ->
                        Friend(
                            photoUri = images?.get(index) ?: Uri.parse(""),
                            user
                        )
                    }
                }
            }

            if (friendsViewModel.currentUser.value != null) {
                friendsViewModel.currentUser.value?.let {user ->
                    item {
                        Text(stringResource(
                            R.string.requests_quantity_text, user.receivedFriendRequests.size),
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }

            if (
                friendsViewModel.requestsList.value != null
                && friendsViewModel.requestsList.value?.size != 0
                && images != null
                && images?.size != 0
                ) {
                friendsViewModel.requestsList.value?.let {requests ->
                    itemsIndexed(requests) {index, user ->
                        FriendRequest(
                            photoUri = friendsViewModel.friendsList.value?.let {list ->
                                images?.get(index + list.size) ?: Uri.parse("")
                            } ?: images?.get(index) ?: Uri.parse(""),
                            user
                        ) {
                            friendsViewModel.currentUser.value?.let { currUser ->
                                currUser.receivedFriendRequests -= user.userId
                                currUser.friends += user.userId
                                friendsViewModel.updateUser(currUser)
                                friendsViewModel.getUserObjectById(user.userId)
                            }
                        }
                    }
                }
            }
        }
    }
}
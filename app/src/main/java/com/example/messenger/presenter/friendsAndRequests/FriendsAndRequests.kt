package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    friendsViewModel: FriendsViewModel,
    navigateToMessages: (String, Uri) -> Unit
) {
    val requestsImages by friendsViewModel.requestsImages.observeAsState()
    val friendsImages by friendsViewModel.friendsImages.observeAsState()
    val currentUser by friendsViewModel.currentUser.observeAsState()
    val friend by friendsViewModel.friend.observeAsState()
    var initialized by rememberSaveable { mutableStateOf(false) }

    if (!initialized) {
        friendsViewModel.getCurrentUserObject()
        friendsViewModel.emitFriendRequests()
        initialized = true
    }

    LaunchedEffect(friend) {
        friendsViewModel.currentUser.value?.let { currUser ->
            friend?.let { friendUser ->
                if (!friendUser.friends.contains(currUser.userId)) {
                    friendUser.friends += currUser.userId
                    friendsViewModel.updateUser(friendUser)
                    friendsViewModel.getCurrentUserObject()
                }
            }
        }
    }

    Column {
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
                        text = stringResource(id = R.string.friends_toolbar_title),
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(vertical = 15.dp)
        ) {
            if (friendsImages != null &&
                friendsImages?.size != 0
                && friendsViewModel.friendsList.value != null
                && friendsViewModel.friendsList.value?.size != 0
                ) {
                friendsViewModel.friendsList.value?.let {friends ->
                    itemsIndexed(friends) {index, user ->
                        Friend(
                            photoUri = friendsImages?.get(index) ?: Uri.parse(""),
                            user,
                            navigateToMessages
                        )
                    }
                }
            }

            if (currentUser != null) {
                item {
                    currentUser?.let {user ->
                        Text(
                            stringResource(
                                R.string.requests_quantity_text, user.receivedFriendRequests.size
                            ),
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }

            if (requestsImages != null &&
                requestsImages?.size != 0
                && friendsViewModel.requestsList.value != null
                && friendsViewModel.requestsList.value?.size != 0
                ) {
                friendsViewModel.requestsList.value?.let {requests ->
                    itemsIndexed(requests) {index, user ->
                        FriendRequest(
                            photoUri = requestsImages?.get(index) ?: Uri.parse(""),
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
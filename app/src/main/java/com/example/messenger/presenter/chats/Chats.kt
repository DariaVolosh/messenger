package com.example.messenger.presenter.chats

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.presenter.components.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(viewModel: ChatsViewModel) {
    val photoUri by viewModel.mainPhotoUri.observeAsState()
    val chats by viewModel.chatList.observeAsState()
    val lastMessages by viewModel.lastMessages.observeAsState()
    val photoUris by viewModel.photoUris.observeAsState()
    val onlineStatus by viewModel.onlineStatus.observeAsState()

    var initialized by rememberSaveable { mutableStateOf(false) }
    var selectedNavItem by remember { mutableIntStateOf(0) }

    if (!initialized) {
        viewModel.fetchChats()
        viewModel.downloadImage()
        initialized = true
    }

    val navigationBarDestinations = listOf("Friends", "Chats", "Settings")
    val navigationBarIcons = listOf(
        R.drawable.ic_chats,
        R.drawable.ic_friends,
        R.drawable.ic_settings
    )

    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Column {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Row {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(0.dp, 0.dp, 15.dp, 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = stringResource(R.string.avatar_content_description),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            )

            if (chats != null && lastMessages != null && photoUris != null && onlineStatus != null) {
                LazyColumn {
                    chats?.let { users ->
                        items(users) { user ->
                            val index = users.indexOf(user)
                            Chat(
                                user.fullName,
                                photoUris?.get(index) ?: Uri.parse(""),
                                lastMessages?.get(index)?.text ?: "",
                                onlineStatus?.get(index) ?: false
                            )
                        }
                    }
                }
            }
        }

        Column (horizontalAlignment = Alignment.End){
            ExtendedFloatingActionButton(
                onClick = {  },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_friend),
                    contentDescription = stringResource(id = R.string.add_friend_fab_text),
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 7.dp, 0.dp)
                        .size(25.dp)
                )

                Text(
                    text = stringResource(R.string.add_friend_fab_text),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                navigationBarDestinations.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNavItem == index,
                        onClick = { selectedNavItem = index },
                        icon = { Icon(
                            painter = painterResource(id = navigationBarIcons[index]),
                            contentDescription = stringResource(R.string.botton_navigation_item_description),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(25.dp)
                        )},
                        label = {
                            Text(
                                text = item,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )

                    )
                }
            }
        }
    }
}
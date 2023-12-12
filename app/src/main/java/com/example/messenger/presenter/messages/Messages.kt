package com.example.messenger.presenter.messages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.data.model.Message
import com.example.messenger.ui.theme.green
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    friendId: String,
    viewModel: MessagesViewModel,
    navigateBack: () -> Unit
) {
    val friend by viewModel.friendObject.observeAsState()
    val friendPhoto by viewModel.friendPhotoUri.observeAsState()
    val online by viewModel.onlineStatus.observeAsState()
    val messages by viewModel.messages.observeAsState()
    val currentUser by viewModel.currentUser.observeAsState()
    var leaveScreenKey by remember { mutableStateOf(false) }
    var initialized by rememberSaveable { mutableStateOf(false) }
    var textMessage by remember { mutableStateOf("") }
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        leaveScreenKey = true
        navigateBack()
    }

    if (!initialized) {
        viewModel.getCurrentUserObject()
        viewModel.downloadFriendMainPhoto(friendId)
        initialized = true
    }

    DisposableEffect(leaveScreenKey) {
        onDispose {
            if (leaveScreenKey) {
                initialized = false
                leaveScreenKey = false
            }
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            viewModel.getUserObjectById(friendId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier.height(75.dp),
            navigationIcon = {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
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
                Row {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(0.dp, 0.dp, 15.dp, 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = friendPhoto,
                            contentDescription = stringResource(R.string.avatar_content_description),
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(50.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Column  {
                            friend?.let { friend ->
                                Text(
                                    friend.fullName,
                                    style = MaterialTheme.typography.displaySmall
                                )
                            }

                            Text(
                                modifier = Modifier.padding(vertical = 7.dp),
                                text =
                                    if (online == true) stringResource(R.string.active_now)
                                    else stringResource(R.string.offline),

                                color = if (online == true) green
                                        else MaterialTheme.colorScheme.error,

                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                }
            }
        )

        LazyColumn (
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = 15.dp,
                    vertical = 15.dp
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = lazyColumnListState
        ){
            items(messages ?: listOf()) { message ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = if (message.from == currentUser?.userId) 25.dp
                            else 0.dp,
                            end = if (message.from == currentUser?.userId) 0.dp
                            else 25.dp
                        ),
                    horizontalAlignment =
                    if (message.from == currentUser?.userId) Alignment.End
                    else Alignment.Start
                ) {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(10.dp),
                        text = message.text,
                        color = MaterialTheme.colorScheme.onTertiary,
                        style = MaterialTheme.typography.displaySmall
                    )

                    val dateFormat = "dd-MM-yyyy HH:mm:ss"
                    val date = Date(message.timestamp)
                    val sdf = SimpleDateFormat(dateFormat)

                    Text(
                        modifier = Modifier.padding(
                            bottom = 10.dp,
                            top = 3.dp
                        ),
                        text = sdf.format(date),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            coroutineScope.launch {
                lazyColumnListState.scrollToItem(messages?.size ?: 0)
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(15.dp, 0.dp, 0.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            Row(
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f)
                    .border(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(25.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = textMessage,
                    onValueChange = { textMessage = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    enabled = true,
                    singleLine = false,
                    textStyle = MaterialTheme.typography.displaySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    decorationBox = {
                        TextFieldDefaults.DecorationBox(
                            value = "",
                            innerTextField = it,
                            singleLine = false,
                            enabled = true,
                            visualTransformation = VisualTransformation.None,
                            placeholder = {
                                if (textMessage.isEmpty()) {
                                    Text(
                                        text =  stringResource(R.string.write_a_message_hint),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }},
                            interactionSource = MutableInteractionSource(),
                            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                                top = 2.dp, bottom = 2.dp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                )
            }
            
            IconButton(
                onClick = {
                    val messageId = viewModel.existingMessagesPath.value?.push()?.key
                    val message = Message(
                        System.currentTimeMillis(),
                        textMessage,
                        currentUser?.userId ?: "",
                        friendId,
                        messageId ?: ""
                    )
                    viewModel.sendMessage(message)
                    viewModel.addChatToChatsList(friendId)
                    textMessage = ""
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = stringResource(R.string.send_content_description),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
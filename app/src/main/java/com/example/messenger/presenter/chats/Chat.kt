package com.example.messenger.presenter.chats

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.data.model.User
import com.example.messenger.ui.theme.green

@Composable
fun Chat(
    user: User,
    lastMessage: String,
    photoUri: Uri,
    onlineStatus: Boolean,
    navigateToChat: (User, Uri) -> Unit
) {
    Row (
        Modifier
            .padding(15.dp, 15.dp, 15.dp, 0.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .height(75.dp)
            .clickable { navigateToChat(user, photoUri) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = photoUri,
            contentDescription = stringResource(R.string.avatar_content_description),
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(verticalArrangement = Arrangement.Center) {
            Row {
                Text(
                    text = user.fullName,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = if (onlineStatus) stringResource(R.string.active_now)
                           else stringResource(R.string.offline),
                    color = if (onlineStatus) green
                            else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
            Text(
                text = lastMessage,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)
            )
        }
    }
}
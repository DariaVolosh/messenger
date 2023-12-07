package com.example.messenger.presenter.components

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.ui.theme.green

@Composable
fun Chat(name: String, photoUri: Uri, lastMessage: String, online: Boolean) {
    Row (
        Modifier
            .padding(15.dp, 15.dp, 15.dp, 0.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .height(75.dp),
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
                    text = name,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = if (online) stringResource(R.string.active_now)
                           else stringResource(R.string.offline),
                    color = if (online) green
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

@Preview
@Composable
fun ChatPreview() {
    MessengerTheme {
        Chat("name", Uri.parse(""), "last message", false)
    }
}
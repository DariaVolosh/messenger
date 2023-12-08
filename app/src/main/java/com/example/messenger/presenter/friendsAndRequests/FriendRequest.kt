package com.example.messenger.presenter.friendsAndRequests
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.data.model.User

@Composable
fun FriendRequest(
    photoUri: Uri,
    user: User,
    addFriend: () -> Unit
) {
    Row (
        Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth()
            .height(75.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
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
                }
                Text(
                    text = user.login,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)
                )
            }
        }

        IconButton(
            onClick = { addFriend() },
            colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .padding(horizontal = 15.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_friend),
                contentDescription = stringResource(id = R.string.add_to_friends_button_text),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
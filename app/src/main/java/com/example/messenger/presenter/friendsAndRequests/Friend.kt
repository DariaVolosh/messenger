package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun Friend(
    photoUri: Uri,
    user: User
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

        Button(onClick = {
        }, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .width(150.dp)
        ) {
            Text(
                text = stringResource(id = R.string.write_a_message_button_text),
                style = MaterialTheme.typography.displaySmall
            )

            Image(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = stringResource(id = R.string.write_a_message_button_text),
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .size(20.dp)
            )
        }
    }
}
package com.example.messenger.presenter.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.ui.theme.MessengerTheme


@Composable
fun HeaderAndDescription(
    modifier: Modifier,
    header: String,
    description: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = header,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 25.dp),
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Preview("headerAndDescription")
@Composable
fun HeaderAndDescriptionPreview() {
    MessengerTheme {
        HeaderAndDescription(
            modifier = Modifier,
            header = "Welcome",
            description = "Login back into your account"
        )
    }
}
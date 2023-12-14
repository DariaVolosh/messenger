package com.example.messenger.presenter.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.messenger.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val currentUser by viewModel.currentUser.observeAsState()
    val imageUrl by viewModel.mainPhotoUri.observeAsState()
    var initialized by remember {
        mutableStateOf(false)
    }

    if (!initialized) {
        viewModel.getCurrentUser()
        initialized = true
    }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(70.dp)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
            model = imageUrl,
            contentDescription = stringResource(id = R.string.main_photo),
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = currentUser?.fullName ?: "",
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = currentUser?.login ?: "",
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = currentUser?.email ?: "",
            style = MaterialTheme.typography.displaySmall
        )
    }
}
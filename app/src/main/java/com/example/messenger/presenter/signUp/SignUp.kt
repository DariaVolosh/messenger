package com.example.messenger.presenter.signUp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.presenter.components.HeaderAndDescription
import com.example.messenger.presenter.components.SignUpSectionWithBackground


@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    navigateToChats: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf(Uri.parse("")) }
    val signedUp by viewModel.signedUp.observeAsState()

    LaunchedEffect(signedUp) {
        signedUp?.let {
            if (it) navigateToChats()
        }
    }

    val localContext = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(), onResult = {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri = result.data?.data
            }
        })

    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {isGranted ->
            if (isGranted) {
                photoPickerLauncher.launch(Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            } else {
                Toast.makeText(
                        localContext,
                getString(localContext, R.string.grant_permission_toast_text),
                Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeaderAndDescription(
            modifier = Modifier,
            header = stringResource(R.string.sign_up_header),
            description = stringResource(R.string.sign_up_description))

        Box(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 25.dp)
                .size(90.dp)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable {
                    if (
                        localContext.checkSelfPermission(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionRequestLauncher
                            .launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        photoPickerLauncher.launch(
                            Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri.toString() == "") {
                Image(
                    painter = painterResource(R.drawable.ic_photo),
                    contentDescription = stringResource(R.string.avatar_content_description),
                    modifier = Modifier.size(35.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = photoUri,
                    contentDescription = stringResource(R.string.avatar_content_description),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(90.dp),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        SignUpSectionWithBackground(
            fullName,
            login,
            password,
            email,
            {newFullName -> fullName = newFullName},
            {newLogin -> login = newLogin},
            {newPassword -> password = newPassword},
            {newEmail -> email = newEmail}) {user, password ->
                viewModel.signUpUser(user, password, photoUri)
            }
    }
}
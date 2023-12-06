package com.example.messenger.presenter.signIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import com.example.messenger.presenter.components.HeaderAndDescription
import com.example.messenger.presenter.components.SignInSectionWithBackground

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    navigateToChats: () -> Unit,
    navigateToSignUpScreen: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val signedIn by viewModel.signedIn.observeAsState()
    val currentUser by viewModel.currentUser.observeAsState()

    LaunchedEffect(signedIn) {
        signedIn?.let {
            if (it) { navigateToChats() }
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            navigateToChats()
        }
    }

    Column (
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        HeaderAndDescription(
            modifier = Modifier,
            header = stringResource(R.string.welcome_message),
            description = stringResource(R.string.login_back_message)
        )

        SignInSectionWithBackground(
            email = email,
            password = password,
            onEmailChange = { newEmail -> email = newEmail},
            onPasswordChange = { newPassword -> password = newPassword}) { email, password ->
            viewModel.signInUser(email, password)
        }

        Text(
            text = stringResource(R.string.do_not_have_an_account),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp)
        )

        TextButton(
            onClick = {navigateToSignUpScreen()},
            modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = stringResource(R.string.join_now),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
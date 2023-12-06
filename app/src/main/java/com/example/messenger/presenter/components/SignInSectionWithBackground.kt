package com.example.messenger.presenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.messenger.R

@Composable
fun SignInSectionWithBackground(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginButtonClick: (String, String) -> Unit
) {
    Column (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldWithIcon(
            input = email,
            modifier = Modifier.padding(0.dp, 55.dp, 0.dp, 25.dp),
                    text = stringResource(R.string.email_hint),
            onInputChange = onEmailChange
        ) {
            Icon(
                painterResource(id = R.drawable.ic_person),
                contentDescription = stringResource(R.string.email_hint),
                modifier = Modifier.size(25.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        TextFieldWithIcon(
            input = password,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 30.dp),
            text = stringResource(R.string.password_hint),
            onInputChange = onPasswordChange
        ) {
            Icon(
                painterResource(id = R.drawable.ic_password),
                contentDescription = stringResource(R.string.password_hint),
                modifier = Modifier.size(25.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Button(
            onClick = { onLoginButtonClick(email, password)},
            modifier = Modifier
                .width(195.dp)
                .height(35.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        ) {
            Text(
                text = stringResource(R.string.login_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.displaySmall
            )
        }

        Text(
            modifier = Modifier.padding(vertical = 15.dp),
            text = stringResource(R.string.forgot_password),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

package com.example.messenger.presenter.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldWithIcon(
    modifier: Modifier,
    text: String,
    onInputChange: (String) -> Unit,
    input: String,
    icon: @Composable () -> Unit, ) {


    OutlinedTextField(
        value = input,
        onValueChange = {onInputChange(it)},
        label = {
            Text(
                text,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        leadingIcon = icon,
        modifier = modifier.height(65.dp),
        textStyle = MaterialTheme.typography.displaySmall,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
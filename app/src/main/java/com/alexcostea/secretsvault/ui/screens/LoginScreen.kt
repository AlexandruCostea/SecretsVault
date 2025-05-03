package com.alexcostea.secretsvault.ui.screens

import androidx.compose.runtime.Composable
import com.alexcostea.secretsvault.domain.entities.Login
import com.alexcostea.secretsvault.ui.forms.LoginForm
import com.alexcostea.secretsvault.viewmodel.SecretsViewModel


@Composable
fun LoginScreen(
    id: Int?,
    viewModel: SecretsViewModel<Login>,
    onBack: () -> Unit,
    onMessage: (String) -> Unit
) {
    SecretScreen(
        id = id,
        secretType = "login",
        newTitle = "New Login",
        editTitle = "Edit Login",
        viewModel = viewModel,
        onBack = onBack,
        onMessage = onMessage,
        formContent = { modifier, initial, onSave, onDelete ->
            LoginForm(
                modifier = modifier,
                initial = initial,
                onSave = onSave,
                onDelete = onDelete
            )
        }
    )
}
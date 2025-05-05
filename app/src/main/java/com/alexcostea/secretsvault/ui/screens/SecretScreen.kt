package com.alexcostea.secretsvault.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.fragment.app.FragmentActivity
import com.alexcostea.secretsvault.domain.entities.Secret
import com.alexcostea.secretsvault.ui.utils.authentication.BiometricAuthenticator
import com.alexcostea.secretsvault.ui.utils.PreferenceUtils
import com.alexcostea.secretsvault.viewmodel.SecretsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Secret> SecretScreen(
    id: Int?,
    secretType: String,
    newTitle: String,
    editTitle: String,
    viewModel: SecretsViewModel<T>,
    onBack: () -> Unit,
    onMessage: (String) -> Unit,
    formContent: @Composable (Modifier, T?, (T) -> Unit, (() -> Unit)?) -> Unit
) {
    val context = LocalContext.current
    var initial by remember { mutableStateOf<T?>(null) }
    var isLoading by remember { mutableStateOf(id != null) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val activity = LocalActivity.current as FragmentActivity


    LaunchedEffect(id) {
        PreferenceUtils.setLastSecretType(context, secretType)
        if (id != null) {
            try {
                BiometricAuthenticator.authenticate(
                    activity = activity,
                    onSuccess = {
                        coroutineScope.launch {
                            try {
                                initial = viewModel.getSecret(id)
                                isLoading = false
                            } catch (e: Exception) {
                                onBack()
                                onMessage("Failed to load $secretType.")
                                isLoading = false
                            }
                        }
                    },
                    onError = {
                        onBack()
                        onMessage("Failed to load $secretType.")
                        isLoading = false
                    },
                )
            } catch (e: Exception) {
                onBack()
                onMessage("Failed to load $secretType.")
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (id == null) newTitle else editTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Text("Loading...", modifier = Modifier.padding(innerPadding))
        } else {
            formContent(
                Modifier.padding(innerPadding),
                initial,
                { secret ->
                    coroutineScope.launch {
                        try {
                            BiometricAuthenticator.authenticate(
                                activity = activity,
                                onSuccess = {
                                    if (secret.id == 0) viewModel.addSecret(secret) else viewModel.updateSecret(secret)
                                    onMessage("$secretType saved successfully.")
                                    keyboardController?.hide()
                                    onBack()
                                },
                                onError = {
                                    onMessage("Failed to save $secretType.")
                                    keyboardController?.hide()
                                    onBack()
                                },
                            )
                        } catch (e: Exception) {
                            onMessage("Failed to save $secretType.")
                            keyboardController?.hide()
                            onBack()
                        }
                    }
                },
                initial?.let { _ ->
                    {
                        coroutineScope.launch {
                            try {
                                BiometricAuthenticator.authenticate(
                                    activity = activity,
                                    onSuccess = {
                                        viewModel.deleteSecret(initial!!.id)
                                        onMessage("$secretType deleted successfully.")
                                        keyboardController?.hide()
                                        onBack()
                                    },
                                    onError = {
                                        onMessage("Failed to delete $secretType.")
                                        keyboardController?.hide()
                                        onBack()
                                    },
                                )
                            } catch (e: Exception) {
                                onMessage("Failed to delete $secretType.")
                                keyboardController?.hide()
                                onBack()
                            }
                        }
                    }
                }
            )
        }
    }
}
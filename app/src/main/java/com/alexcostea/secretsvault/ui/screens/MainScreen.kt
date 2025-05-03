package com.alexcostea.secretsvault.ui.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alexcostea.secretsvault.domain.entities.Secret
import com.alexcostea.secretsvault.ui.utils.SecretType
import com.alexcostea.secretsvault.viewmodel.SecretsViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.alexcostea.secretsvault.ui.items.SecretPreviewItem
import com.alexcostea.secretsvault.ui.utils.PreferenceUtils
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.alexcostea.secretsvault.domain.factories.LoginFactory

@Composable
fun MainScreen(
    viewModels: Map<SecretType, SecretsViewModel<out Secret>>,
    onNavigateToForm: (SecretType, Int?) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(SecretType.LOGIN) }
    val viewModel = viewModels[selectedType]!!
    val secrets by viewModel.previews.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val secretTypes = SecretType.entries
    var currentIndex = secretTypes.indexOf(selectedType)

    val swipeModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
            when {
                // Swipe right (positive dragAmount) - go to previous tab
                dragAmount > 50f && currentIndex > 0 -> {
                    coroutineScope.launch {
                        selectedType = secretTypes[currentIndex - 1]
                        currentIndex = secretTypes.indexOf(selectedType)
                        PreferenceUtils.setLastSecretType(context, selectedType.name.lowercase())
                    }
                }
                // Swipe left (negative dragAmount) - go to next tab
                dragAmount < -50f && currentIndex < secretTypes.size - 1 -> {
                    coroutineScope.launch {
                        selectedType = secretTypes[currentIndex + 1]
                        currentIndex = secretTypes.indexOf(selectedType)
                        PreferenceUtils.setLastSecretType(context, selectedType.name.lowercase())
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedType) {
        when (PreferenceUtils.getLastSecretType(context)) {
            "login" -> selectedType = SecretType.LOGIN
            "note" -> selectedType = SecretType.NOTE
        }
        try {
            viewModel.loadSecrets()
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Failed to load secrets. Please try again by reloading the application.")
        }
    }

    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ){
                TabRow(
                    selectedTabIndex = currentIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    secretTypes.forEachIndexed { index, type ->
                        Tab(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                PreferenceUtils.setLastSecretType(context, selectedType.name.lowercase())

                            },
                            text = {
                                Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigateToForm(selectedType, null)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Secret")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(swipeModifier)
        ) {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(secrets) { secret ->
                    SecretPreviewItem(
                        secret,
                        onClick = { onNavigateToForm(selectedType, secret.id) }
                    )
                }
            }
        }
    }
}
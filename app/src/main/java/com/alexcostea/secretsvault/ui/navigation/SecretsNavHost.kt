package com.alexcostea.secretsvault.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexcostea.secretsvault.domain.entities.Login
import com.alexcostea.secretsvault.domain.entities.Note
import com.alexcostea.secretsvault.domain.entities.Secret
import com.alexcostea.secretsvault.ui.screens.LoginScreen
import com.alexcostea.secretsvault.ui.screens.MainScreen
import com.alexcostea.secretsvault.ui.screens.NoteScreen
import com.alexcostea.secretsvault.ui.utils.SecretType
import com.alexcostea.secretsvault.viewmodel.SecretsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Note : Screen("note/{id}") {
        fun createRoute(id: Int?) = "note/${id ?: -1}"
    }
    data object Login : Screen("login/{id}") {
        fun createRoute(id: Int?) = "login/${id ?: -1}"
    }
}


@Composable
fun SecretsNavHost(viewModels: Map<SecretType, SecretsViewModel<out Secret>>) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            val job = launch {
                snackbarHostState.showSnackbar(it)
            }
            delay(800)
            job.cancel()
            snackbarMessage = null
        }
    }

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModels,
                onNavigateToForm = { type, id ->
                    val route = when (type) {
                        SecretType.NOTE -> Screen.Note.createRoute(id)
                        SecretType.LOGIN -> Screen.Login.createRoute(id)
                    }
                    navController.navigate(route)
                },
                snackbarHostState = snackbarHostState
            )
        }

        composable(
            Screen.Note.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")?.takeIf { it >= 0 }
            val vm = viewModels[SecretType.NOTE] as SecretsViewModel<Note>
            NoteScreen(
                id = id,
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onMessage = { snackbarMessage = it }
            )
        }

        composable(
            Screen.Login.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")?.takeIf { it >= 0 }
            val vm = viewModels[SecretType.LOGIN] as SecretsViewModel<Login>
            LoginScreen(
                id = id,
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onMessage = { snackbarMessage = it }
            )
        }
    }
}


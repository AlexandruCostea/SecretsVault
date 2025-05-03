package com.alexcostea.secretsvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.alexcostea.secretsvault.database.connection.MobileDatabaseConnectionManager
import com.alexcostea.secretsvault.database.schema.initializer.CompositeTableInitializer
import com.alexcostea.secretsvault.database.schema.initializer.SingleTableInitializer
import com.alexcostea.secretsvault.database.schema.initializer.TableInitializer
import com.alexcostea.secretsvault.database.schema.table.LoginTable
import com.alexcostea.secretsvault.database.schema.table.NoteTable
import com.alexcostea.secretsvault.database.schema.table.UserTable
import com.alexcostea.secretsvault.domain.entities.Login
import com.alexcostea.secretsvault.domain.entities.Note
import com.alexcostea.secretsvault.domain.factories.LoginFactory
import com.alexcostea.secretsvault.domain.factories.NoteFactory
import com.alexcostea.secretsvault.domain.factories.SecretFactory
import com.alexcostea.secretsvault.domain.factories.SecretSummaryFactory
import com.alexcostea.secretsvault.repository.secret.LoginRepository
import com.alexcostea.secretsvault.repository.secret.NoteRepository
import com.alexcostea.secretsvault.repository.secret.SecretRepository
import com.alexcostea.secretsvault.repository.user.UserDataRepository
import com.alexcostea.secretsvault.repository.user.UserRepository
import com.alexcostea.secretsvault.service.authentication.AuthenticationService
import com.alexcostea.secretsvault.service.authentication.UserAuthenticationService
import com.alexcostea.secretsvault.service.encryption.EncryptionService
import com.alexcostea.secretsvault.service.encryption.SecretsEncryptionService
import com.alexcostea.secretsvault.service.secrets.LoginSecretService
import com.alexcostea.secretsvault.service.secrets.NoteSecretService
import com.alexcostea.secretsvault.service.secrets.SecretService
import com.alexcostea.secretsvault.ui.navigation.SecretsNavHost
import com.alexcostea.secretsvault.ui.theme.SecretsVaultTheme
import com.alexcostea.secretsvault.ui.utils.SecretType
import com.alexcostea.secretsvault.viewmodel.SecretsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileDatabaseConnectionManager.initialize(this)
        initDb()

        val previewFactory: SecretFactory = SecretSummaryFactory()
        val loginFactory: SecretFactory = LoginFactory()
        val noteFactory: SecretFactory = NoteFactory()

        val userRepo: UserRepository =
            UserDataRepository()
        val loginRepo: SecretRepository =
            LoginRepository(
                previewFactory,
                loginFactory
            )
        val noteRepo: SecretRepository =
            NoteRepository(
                previewFactory,
                noteFactory
            )

        val loginService: SecretService = LoginSecretService(loginRepo)
        val noteService: SecretService = NoteSecretService(noteRepo)
        val encryptionService: EncryptionService =
            SecretsEncryptionService(
                listOf(
                    loginService,
                    noteService
                )
            )
        val mobileAuthenticationService: AuthenticationService =
            UserAuthenticationService(
                userRepo, encryptionService
            )

        val loginViewModel: SecretsViewModel<Login> = SecretsViewModel(loginService)
        val noteViewModel: SecretsViewModel<Note> = SecretsViewModel(noteService)
        mobileAuthenticationService.login().get()


        loginViewModel.loadSecrets()
        noteViewModel.loadSecrets()



        enableEdgeToEdge()
        setContent {
            SecretsVaultTheme {
                val loginVM = remember { loginViewModel }
                val noteVM = remember { noteViewModel }

                val viewModels = mapOf(
                    SecretType.LOGIN to loginVM,
                    SecretType.NOTE to noteVM
                )


                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SecretsNavHost(viewModels)
                }

            }
        }

    }

    private fun initDb() {
        val userTableInitializer: TableInitializer =
            SingleTableInitializer(
                UserTable().query
            )

        val loginTableInitializer: TableInitializer =
            SingleTableInitializer(
                LoginTable().query
            )

        val noteTableInitializer: TableInitializer =
            SingleTableInitializer(
                NoteTable().query
            )

        val secretTableInitializer: TableInitializer =
            CompositeTableInitializer(
                listOf(
                    loginTableInitializer,
                    noteTableInitializer
                )
            )

        val fullDbInitializer: TableInitializer =
            CompositeTableInitializer(
                listOf(
                    userTableInitializer,
                    secretTableInitializer
                )
            )

        fullDbInitializer.initialize()
    }
}
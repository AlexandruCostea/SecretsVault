package com.alexcostea.secretsvault.ui.screens

import androidx.compose.runtime.Composable
import com.alexcostea.secretsvault.domain.entities.Note
import com.alexcostea.secretsvault.ui.forms.NoteForm
import com.alexcostea.secretsvault.viewmodel.SecretsViewModel

@Composable
fun NoteScreen(
    id: Int?,
    viewModel: SecretsViewModel<Note>,
    onBack: () -> Unit,
    onMessage: (String) -> Unit
) {
    SecretScreen(
        id = id,
        secretType = "note",
        newTitle = "New Note",
        editTitle = "Edit Note",
        viewModel = viewModel,
        onBack = onBack,
        onMessage = onMessage,
        formContent = { modifier, initial, onSave, onDelete ->
            NoteForm(
                modifier = modifier,
                initial = initial,
                onSave = onSave,
                onDelete = onDelete
            )
        }
    )
}
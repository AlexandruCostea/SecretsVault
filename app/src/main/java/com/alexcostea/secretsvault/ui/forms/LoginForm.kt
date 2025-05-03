package com.alexcostea.secretsvault.ui.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexcostea.secretsvault.domain.entities.Login
import com.alexcostea.secretsvault.domain.factories.LoginFactory

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    initial: Login? = null,
    onSave: (Login) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var mailOrUsername by remember { mutableStateOf(initial?.mailOrUsername ?: "") }
    var password by remember { mutableStateOf(initial?.password ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = mailOrUsername,
            onValueChange = { mailOrUsername = it },
            label = { Text("Email / Username") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onDelete != null) {
                OutlinedButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Button(
                onClick = {
                    val updated = Login(
                        initial?.id ?: 0,
                        title,
                        System.currentTimeMillis().toString(),
                        mailOrUsername,
                        password
                    )
                    onSave(updated)
                },
                enabled = title.isNotBlank()
            ) {
                Icon(Icons.Default.Create, contentDescription = "Save")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save")
            }
        }
    }
}
package com.alexcostea.secretsvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexcostea.secretsvault.domain.entities.Secret
import com.alexcostea.secretsvault.service.secrets.SecretService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecretsViewModel<T : Secret>(
    private val service: SecretService
) : ViewModel() {

    private val _previews = MutableStateFlow<List<Secret>>(emptyList())
    val previews: StateFlow<List<Secret>> = _previews.asStateFlow()


    fun loadSecrets() {
        viewModelScope.launch {
            val previews = withContext(Dispatchers.IO) {
                service.getSecretPreviews().get().toList()
            }
            _previews.value = previews
        }
    }

    suspend fun getSecret(id: Int): T? = withContext(Dispatchers.IO) {
        val secret = service.getFullSecret(id).get()
        secret as? T
    }

    fun addSecret(secret: T) {
        viewModelScope.launch {
            val added = withContext(Dispatchers.IO) {
                service.addSecret(secret).get()
            }
            added?.let {
                _previews.value = _previews.value.toMutableList().apply { add(it) }
            }
        }
    }

    fun updateSecret(secret: T) {
        viewModelScope.launch {
            val updated = withContext(Dispatchers.IO) {
                service.updateSecret(secret).get()
            }
            updated?.let {
                _previews.value = _previews.value.map { if (it.id == secret.id) updated else it }
            }
        }
    }

    fun deleteSecret(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                service.deleteSecret(id).get()
            }
            _previews.value = _previews.value.filter { it.id != id }
        }
    }
}
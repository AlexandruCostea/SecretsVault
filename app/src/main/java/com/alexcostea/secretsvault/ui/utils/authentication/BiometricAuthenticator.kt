package com.alexcostea.secretsvault.ui.utils.authentication

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricAuthenticator {
    enum class AuthenticatorType {
        BIOMETRIC_ONLY,
        DEVICE_CREDENTIAL,
        BIOMETRIC_OR_DEVICE_CREDENTIAL
    }

    fun authenticate(
        activity: FragmentActivity,
        authenticatorType: AuthenticatorType = AuthenticatorType.BIOMETRIC_OR_DEVICE_CREDENTIAL,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFallbackRequested: (() -> Unit)? = null
    ) {
        when (authenticatorType) {
            AuthenticatorType.BIOMETRIC_ONLY -> showBiometricPrompt(activity, false, onSuccess, onError, onFallbackRequested)
            AuthenticatorType.DEVICE_CREDENTIAL -> showDeviceCredentialPrompt(activity, onSuccess, onError)
            AuthenticatorType.BIOMETRIC_OR_DEVICE_CREDENTIAL -> {
                if (isBiometricAvailable(activity)) {
                    showBiometricPrompt(activity, true, onSuccess, onError, onFallbackRequested)
                } else {
                    showDeviceCredentialPrompt(activity, onSuccess, onError)
                }
            }
        }
    }

    private fun showBiometricPrompt(
        activity: FragmentActivity,
        allowDeviceCredential: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFallbackRequested: (() -> Unit)? = null
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication Required")
            .setSubtitle("Please authenticate to proceed")

        if (allowDeviceCredential) {
            promptInfoBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptInfoBuilder.setNegativeButtonText("Cancel")
        }

        val promptInfo = promptInfoBuilder.build()

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            onFallbackRequested?.invoke()
                        }
                        else -> onError(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    onError("Authentication failed")
                }
            })

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError("Authentication failed: ${e.message}")
            onFallbackRequested?.invoke()
        }
    }

    private fun showDeviceCredentialPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication Required")
            .setSubtitle("Please enter your PIN, pattern, or password")
            .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    onError("Authentication failed")
                }
            })

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            // Fallback to system settings if device credential prompt fails
            activity.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
            onError("Please set up a lock screen method (PIN/Pattern/Password) in Settings")
        }
    }

    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isDeviceCredentialAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isAnyAuthMethodAvailable(context: Context): Boolean {
        return isBiometricAvailable(context) || isDeviceCredentialAvailable(context)
    }
}
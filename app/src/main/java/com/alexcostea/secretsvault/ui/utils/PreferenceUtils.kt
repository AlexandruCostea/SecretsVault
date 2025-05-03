package com.alexcostea.secretsvault.ui.utils

import android.content.Context
import androidx.core.content.edit

object PreferenceUtils {
    private const val PREF_NAME = "secrets_vault_prefs"
    private const val KEY_LAST_SECRET_TYPE = "last_secret_type"

    fun setLastSecretType(context: Context, type: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LAST_SECRET_TYPE, type) }
    }

    fun getLastSecretType(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LAST_SECRET_TYPE, null)
    }
}
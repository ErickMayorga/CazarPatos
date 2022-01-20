package com.mayorgaerick.cazarpatos

import android.app.Activity
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.EncryptedSharedPreferences

class EncryptedSharedPreferences(val actividad: Activity): FileHandler {
    //Validar si existen datos en archivo de preferencia, y cargar
    val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",//filename
        masterKeyAlias,
        actividad,//context
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun SaveInformation(datosAGrabar: Pair<String, String>) {
        val editor = sharedPreferences.edit()
        editor.putString(datosAGrabar.first,datosAGrabar.second)
        editor.apply()

    }

    override fun ReadInformation(): Pair<String, String> {
        val email = sharedPreferences.getString(LOGIN_KEY,"").toString()
        val clave = sharedPreferences.getString(PASSWORD_KEY,"").toString()
        return (email to clave)

    }
}
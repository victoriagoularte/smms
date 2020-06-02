package br.com.unb.smms.security

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecurityConstants {
    companion object{
        val FILE_SHARED_PREF = "prefSmms"
        val LOGIN_RESULT = ".loginResult"
        val ACCESS_TOKEN = ".access_token"
        val PAGE_INFO = ".pageInfo"
    }
}

fun getEncrypSharedPreferences(context: Context): SharedPreferences {
    return if (context.getSharedPreferences(SecurityConstants.FILE_SHARED_PREF, MODE_PRIVATE) != null) {
        context.getSharedPreferences(SecurityConstants.FILE_SHARED_PREF, MODE_PRIVATE)
    } else {
        EncryptedSharedPreferences
            .create(
                SecurityConstants.FILE_SHARED_PREF,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }
}


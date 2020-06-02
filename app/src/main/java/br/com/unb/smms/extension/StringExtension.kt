package br.com.unb.smms.extension

import com.google.gson.GsonBuilder

fun String.isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.toRegex().matches(this)

inline fun <reified T> String.fromJson() = GsonBuilder().create().fromJson(this, T::class.java)

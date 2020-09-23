package br.com.unb.smms.extension

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun String.isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.toRegex().matches(this)

inline fun <reified T> String.fromJson() = GsonBuilder().create().fromJson(this, T::class.java)

fun String.toLocalDateTime() : Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).parse(this)!!
    return Date.parse(this, formatter)
}

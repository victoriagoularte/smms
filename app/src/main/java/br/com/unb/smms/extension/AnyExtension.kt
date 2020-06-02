package br.com.unb.smms.extension

import com.google.gson.GsonBuilder

fun Any.toJson(): String = GsonBuilder().create().toJson(this)
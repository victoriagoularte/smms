package br.com.unb.smms

class SmmsException(override val message: String? = null, val code: Int? = null) :
    Exception(message)
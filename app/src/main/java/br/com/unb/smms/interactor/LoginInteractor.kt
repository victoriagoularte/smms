package br.com.unb.smms.interactor

import android.content.Context
import br.com.unb.smms.R
import br.com.unb.smms.SmmsException
import br.com.unb.smms.extension.isValidEmail
import br.com.unb.smms.repository.LoginRepository
import io.reactivex.Single

class LoginInteractor(val context: Context) {

    companion object {
        const val INVALID_USERID = -9999
    }

    private val loginRepository = LoginRepository()

    fun singIn(email: String, password: String): String? {
        return try {
            if (!email.isValidEmail()) {
                throw SmmsException(context.getString(R.string.invalid_email), INVALID_USERID)
            } else {
                loginRepository.signIn(email, password)
            }
        } catch (e: Exception) {
            error(e)
        }
    }

    fun register(email: String, password: String): String? {
        return try {
            if (!email.isValidEmail()) {
                throw SmmsException(context.getString(R.string.invalid_email), INVALID_USERID)
            } else {
                loginRepository.register(email, password)
            }
        } catch (e: Exception) {
            error(e)
        }
    }


}
package br.com.unb.smms.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.SmmsData
import br.com.unb.smms.interactor.LoginInteractor


class LoginViewModel(val app: Application) : AndroidViewModel(app) {

    private val interactor = LoginInteractor(app.applicationContext)

    val password = MutableLiveData<String>("11223344")
    val email = MutableLiveData<String>("vickgoularte@gmail.com")
    val resultSignIn = MutableLiveData<SmmsData<String>>()
    val resultRegister = MutableLiveData<SmmsData<String>>()

    fun signIn() {

        if (email.value != null && password.value != null) {
            val error = interactor.singIn(email.value!!, password.value!!)

            if (error != null) {
                resultSignIn.value = SmmsData.Error(Throwable(error))
                return
            }

            resultSignIn.value = SmmsData.Success("success")

        }
    }

    fun register() {

        if (email.value != null && password.value != null) {
            val error = interactor.register(email.value!!, password.value!!)

            if (error != null) {
                resultSignIn.value = SmmsData.Error(Throwable(error))
                return
            }

            resultSignIn.value = SmmsData.Success("success")
        }
    }

    override fun onCleared() {
        super.onCleared()

    }

}
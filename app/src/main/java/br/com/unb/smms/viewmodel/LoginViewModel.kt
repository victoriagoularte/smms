package br.com.unb.smms.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.SmmsData
import br.com.unb.smms.interactor.LoginInteractor


class LoginViewModel(val app: Application) : AndroidViewModel(app) {

    private val interactor = LoginInteractor(app.applicationContext)

    val password = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val resultSignIn = MutableLiveData<SmmsData<String>>()
    val resultRegister = MutableLiveData<SmmsData<String>>()

}
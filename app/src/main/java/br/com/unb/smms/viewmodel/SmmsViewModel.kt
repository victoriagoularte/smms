package br.com.unb.smms.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.Account
import br.com.unb.smms.extension.toJson
import br.com.unb.smms.interactor.UserInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class SmmsViewModel @ViewModelInject constructor(
    private val userInteractor: UserInteractor,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var pageInfoDisposable: Disposable
    private lateinit var picDisposable: Disposable

    var resultPic = MutableLiveData<SmmsData<Bitmap>>()
    var resultAccess = MutableLiveData<SmmsData<Account>>()


    fun access() {

        pageInfoDisposable = userInteractor.access().subscribe{ res, error ->
            if (error != null) {
                resultAccess.value =
                    SmmsData.Error(Throwable("erro ao carregar informações da pagina"))
                return@subscribe
            }

            resultAccess.value = SmmsData.Success(res)

            getEncrypSharedPreferences(context).edit()
                .putString(SecurityConstants.PAGE_INFO, res!!.toJson()).apply()

        }

        smmsCompositeDisposable.add(pageInfoDisposable)
    }

    fun getUserProfilePicture() {
        picDisposable = userInteractor.getUserProfilePicture().subscribe { res, error ->
            if (error != null) {
                resultPic.value = SmmsData.Error(Throwable("erro ao carregar imagem"))
                return@subscribe
            }

            resultPic.value = SmmsData.Success(res)
        }

        smmsCompositeDisposable.add(picDisposable)

    }


    override fun onCleared() {
        super.onCleared()
        smmsCompositeDisposable.dispose()
    }


}

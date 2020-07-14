package br.com.unb.smms.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.SmmsData
import br.com.unb.smms.extension.toJson
import br.com.unb.smms.interactor.UserInteractor
import br.com.unb.smms.domain.NodeGraph
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class SmmsViewModel(val app: Application) : AndroidViewModel(app) {

    private val userInteractor = UserInteractor(app.applicationContext)
    private val pageInteractor = PageInteractor(app.applicationContext)

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var pageInfoDisposable: Disposable
    private lateinit var feedDisposable: Disposable
    private lateinit var picDisposable: Disposable

    var resultPageInfo = MutableLiveData<SmmsData<String>>()
    var resultPic = MutableLiveData<SmmsData<Bitmap>>()
    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()


    fun access() {

        pageInfoDisposable = userInteractor.access().subscribe{ res, error ->
            if(error != null) {
                resultPost.value = SmmsData.Error(Throwable("erro ao carregar informações da pagina"))
                return@subscribe
            }

            getEncrypSharedPreferences(app.baseContext).edit()
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

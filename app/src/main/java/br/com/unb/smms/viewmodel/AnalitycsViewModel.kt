package br.com.unb.smms.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.Feed
import br.com.unb.smms.domain.NodeGraph
import br.com.unb.smms.extension.toJson
import br.com.unb.smms.interactor.SmmsPageInteractor
import br.com.unb.smms.interactor.SmmsUserInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AnalitycsViewModel(val app: Application) : AndroidViewModel(app) {

    private val userInteractor = SmmsUserInteractor(app.applicationContext)
    private val pageInteractor = SmmsPageInteractor(app.applicationContext)

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var pageInfoDisposable: Disposable
    private lateinit var feedDisposable: Disposable
    private lateinit var picDisposable: Disposable
    private lateinit var friendsDisposable: Disposable

    var resultPageInfo = MutableLiveData<SmmsData<String>>()
    var resultPic = MutableLiveData<SmmsData<Bitmap>>()
    var resultFriends = MutableLiveData<String>("0")
    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()


    fun access() {
        pageInfoDisposable = userInteractor.access(getAccessToken().userId).subscribe{ res, error ->
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
        picDisposable = userInteractor.getUserProfilePicture(getAccessToken().userId).subscribe { res, error ->
            if (error != null) {
                resultPic.value = SmmsData.Error(Throwable("erro ao carregar imagem"))
                return@subscribe
            }

            resultPic.value = SmmsData.Success(res)
        }

        smmsCompositeDisposable.add(picDisposable)

    }

    fun getFriendsCount() {
        friendsDisposable = userInteractor.getFriendsCount(getAccessToken().userId)
            .subscribe { res, error ->
                if (res?.summary != null)
                    resultFriends.value = (res.summary!!.totalCount).toString()

            }

        smmsCompositeDisposable.add(friendsDisposable)

    }

    private fun getAccessToken(): AccessToken {
        val gson = Gson()
        val loginResultString = getEncrypSharedPreferences(app.baseContext).getString(
            SecurityConstants.LOGIN_RESULT,
            ""
        )

        val loginResult = gson.fromJson(loginResultString, LoginResult::class.java)
        return loginResult.accessToken
    }

    override fun onCleared() {
        super.onCleared()
        smmsCompositeDisposable.dispose()
    }
}
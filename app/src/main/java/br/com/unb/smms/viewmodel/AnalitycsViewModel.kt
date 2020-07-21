package br.com.unb.smms.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.interactor.IgInteractor
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.interactor.UserInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AnalitycsViewModel(val app: Application) : AndroidViewModel(app) {

    private val userInteractor = UserInteractor(app.applicationContext)
    private val pageInteractor = PageInteractor(app.applicationContext)
    private val igInteractor = IgInteractor(app.applicationContext)

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var friendsFacebookDisposable: Disposable
    private lateinit var instaInfoDisposable: Disposable

    var resultFacebookFriends = MutableLiveData<String>()
    var followersCount = MutableLiveData<String>()

    var resultUserIdIg = MutableLiveData<SmmsData<NodeGraph>>()
    var resultInstaInfo = MutableLiveData<SmmsData<IgInfo>>()

    fun getFriendsCount() {
        friendsFacebookDisposable = userInteractor.getFriendsCount()
            .subscribe { res, error ->
                if (res?.summary != null)
                    resultFacebookFriends.value = (res.summary!!.totalCount).toString()

            }

        smmsCompositeDisposable.add(friendsFacebookDisposable)

    }

    fun userIdIg() {

        instaInfoDisposable = pageInteractor.igBusinessAccount()
            .subscribe { res, error ->
                if(error != null) {
                    resultUserIdIg.value = SmmsData.Error(error)
                    return@subscribe
                }

                getEncrypSharedPreferences(app.baseContext).edit()
                    .putString(SecurityConstants.IG_BUSINESS_ACCOUNT, res!!.igBusinessAccount!!.id).apply()

                resultUserIdIg.value = SmmsData.Success(res.igBusinessAccount!!)
                infoIg()
            }

        smmsCompositeDisposable.add(instaInfoDisposable)


    }

    fun infoIg() {

        igInteractor.igInfo()
            .subscribe { res, error ->
                if(error != null) {
                    resultInstaInfo.value = SmmsData.Error(error)
                    return@subscribe
                }

                followersCount.value = res.followersCount.toString()

            }



    }



    override fun onCleared() {
        super.onCleared()
        smmsCompositeDisposable.dispose()
    }
}
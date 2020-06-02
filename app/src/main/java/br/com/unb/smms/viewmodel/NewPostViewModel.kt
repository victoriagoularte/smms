package br.com.unb.smms.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.Feed
import br.com.unb.smms.domain.NodeGraph
import br.com.unb.smms.interactor.SmmsPageInteractor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class NewPostViewModel(val app: Application) : AndroidViewModel(app) {

    private val pageInteractor = SmmsPageInteractor(app.applicationContext)

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var feedDisposable: Disposable

    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()

    val text = MutableLiveData<String>()
    val textErrorMessage = MutableLiveData<String>()

    fun feed(downloadUri: String?) {

        val feed = if (downloadUri != null) {
            Feed(text.value, downloadUri)
        } else {
            Feed(text.value)
        }


        textErrorMessage.value = pageInteractor.validateTextPost(feed)
        if (!textErrorMessage.value.isNullOrEmpty()) return

        feedDisposable = pageInteractor.feed(app.baseContext.getString(R.string.page_id), feed)
            .subscribe { res, error ->
                resultPost.value = SmmsData.Success(res!!)
            }

        smmsCompositeDisposable.add(feedDisposable)
    }


}
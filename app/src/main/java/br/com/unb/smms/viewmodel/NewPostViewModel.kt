package br.com.unb.smms.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class NewPostViewModel(val app: Application) : AndroidViewModel(app) {

    private lateinit var database: DatabaseReference

    private val pageInteractor = PageInteractor(app.applicationContext)
    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var feedDisposable: Disposable

    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()

    val text = MutableLiveData<String>()
    val textErrorMessage = MutableLiveData<String>()

    fun feed(downloadUri: Uri?) {

        var feed = Feed(text.value)

        if (downloadUri != null) {
            feed.url = downloadUri.toString()
        }

        textErrorMessage.value = pageInteractor.validateTextPost(feed)
        if (!textErrorMessage.value.isNullOrEmpty()) return

        if (downloadUri == null) {
            feedDisposable = pageInteractor.feed(feed)
                .subscribe { res, _ ->
                    writeNewPost(downloadUri)
                    resultPost.value = SmmsData.Success(res!!)
                }

        } else {
            feedDisposable = pageInteractor.photo(feed)
                .subscribe { res, _ ->
                    writeNewPost(downloadUri.toString())
                    resultPost.value = SmmsData.Success(res!!)
                }
        }

        smmsCompositeDisposable.add(feedDisposable)
    }

    private fun writeNewPost(downloadUri: String?) {

        val database = FirebaseDatabase.getInstance().reference

        val newPostRef = database.child("posts").push()
        val post = Post(getUid(), "teste", text.value, downloadUri)
        newPostRef.setValue(post)

    }

    private fun getUid(): String? {
        return getEncrypSharedPreferences(app.baseContext).getString(
            SecurityConstants.UID_FIREBASE,
            null
        )
    }




}
package br.com.unb.smms.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.extension.toString
import br.com.unb.smms.interactor.FirebaseInteractor
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

class NewPostViewModel(val app: Application) : AndroidViewModel(app) {

    private val pageInteractor = PageInteractor(app.applicationContext)
    private val firebaseInteractor = FirebaseInteractor(app.applicationContext)

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var feedDisposable: Disposable
    private lateinit var photoDisposable: Disposable

    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()
    var resultUploadPhoto = MutableLiveData<SmmsData<Uri>>()

    val uriPhoto = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val text = MutableLiveData<String>()
    val textPost = MutableLiveData<String>()
    val tags = MutableLiveData<String>()
    val postFacebook = MutableLiveData<Boolean>(false)
    val postInsta = MutableLiveData<Boolean>(false)
    val postInstaStory = MutableLiveData<Boolean>(false)
    val textErrorMessage = MutableLiveData<String>()
    var categorySelected = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun feed(downloadUri: String?) {

        _dataLoading.value = true

        var tagsPost : String
        var feed : Feed? = null

        if(tags.value != null) {
            tagsPost = "#" + (tags.value)?.split(", ", ",")?.distinct()!!.joinToString(" #")
            textPost.value = text.value + "\n.\n.\n.\n.\n.\n" + tagsPost
            feed = Feed(textPost.value)
        } else {
            feed = Feed(text.value)
        }

//        textErrorMessage.value = pageInteractor.validateTextPost(feed)
//        if (!textErrorMessage.value.isNullOrEmpty()) return

        if (downloadUri == null) {
            feedDisposable = pageInteractor.feed(feed)
                .subscribe { res, _ ->
                    if (res != null) {
                        writeNewPost(res.id!!, downloadUri)
                        resultPost.value = SmmsData.Success(res)
                    }
                }

        } else {
            feed.url = downloadUri
            feedDisposable = pageInteractor.photo(feed)
                .subscribe { res, _ ->
                    if (res != null) {
                        writeNewPost(res.id!!, downloadUri)
                        resultPost.value = SmmsData.Success(res)
                    }
                }
        }

        smmsCompositeDisposable.add(feedDisposable)
    }

    private fun writeNewPost(postId: String, downloadUri: String?) {

        val currentDate = Calendar.getInstance().time
        val post = Post(
            getUid(),
            title.value,
            text.value,
            postId,
            downloadUri,
            currentDate.toString("MM/yyyy"),
            category = categorySelected.value
        )

        firebaseInteractor.writeNewPost(tags.value, post)
        _dataLoading.value = false

    }

    fun uploadFirebaseImage() {
        _dataLoading.value = true

        photoDisposable = firebaseInteractor.uploadImageFirebase(uriPhoto.value!!).subscribe { res, error ->
            if (res != null) {
                resultUploadPhoto.value = SmmsData.Success(res)
                uriPhoto.value = res.toString()
                return@subscribe
            }

            resultUploadPhoto.value = SmmsData.Error(error)
        }

        smmsCompositeDisposable.add(photoDisposable)
    }

    fun validateFields(): Boolean {

        if(postFacebook.value!! && tags.value == null && text.value == null && uriPhoto.value == null) {
            return false
        } else if(postInsta.value!! && uriPhoto.value == null) {
            return false
        }

        return true
    }

    private fun getUid(): String? {
        return getEncrypSharedPreferences(app.baseContext).getString(
            SecurityConstants.UID_FIREBASE,
            null
        )
    }

    override fun onCleared() {
        super.onCleared()
        smmsCompositeDisposable.dispose()
    }


}
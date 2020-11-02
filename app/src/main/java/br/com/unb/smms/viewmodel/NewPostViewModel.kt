package br.com.unb.smms.viewmodel

import android.content.Context
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.domain.firebase.Tag
import br.com.unb.smms.extension.toString
import br.com.unb.smms.interactor.FirebaseInteractor
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*


class NewPostViewModel @ViewModelInject constructor(val pageInteractor: PageInteractor, val firebaseInteractor: FirebaseInteractor, @ApplicationContext val context: Context) : ViewModel() {

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var feedDisposable: Disposable
    private lateinit var photoDisposable: Disposable

    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()
    var resultUploadPhoto = MutableLiveData<SmmsData<Uri>>()

    val uriPhoto = MutableLiveData<String>()
    val downloadPhotoUrl = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val text = MutableLiveData<String>()
    val textPost = MutableLiveData<String>()
    val tags = MutableLiveData<String>()
    val postFacebook = MutableLiveData<Boolean>(false)
    val postInsta = MutableLiveData<Boolean>(false)
    val postInstaStory = MutableLiveData<Boolean>(false)
    val pendingProcess = MutableLiveData(false)
    val datePending = MutableLiveData<Date>()
    val textErrorMessage = MutableLiveData<String>()
    var categorySelected = MutableLiveData<String>()

    var postPendingDate = MutableLiveData<Date>()
    var postPending = MutableLiveData<Post>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun feed() {

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

        if (downloadPhotoUrl.value == null) {
            feedDisposable = pageInteractor.feed(feed)
                .subscribe { res, _ ->
                    if (res != null) {
                        writeNewPost(res.id!!, downloadPhotoUrl.value)
                        resultPost.value = SmmsData.Success(res)
                    }
                }

        } else {
            feed.url = downloadPhotoUrl.value
            feedDisposable = pageInteractor.photo(feed)
                .subscribe { res, _ ->
                    if (res != null) {
                        writeNewPost(res.id!!, downloadPhotoUrl.value)
                        resultPost.value = SmmsData.Success(res)
                    }
                }
        }

        smmsCompositeDisposable.add(feedDisposable)
    }

    private fun writeNewPost(postId: String, downloadUri: String?) {

        val date = Calendar.getInstance().time
        var platforms: MutableList<String> = arrayListOf()

        postFacebook.value?.let { it -> if(it) platforms.add("facebook") }
        postInsta.value?.let { it -> if(it) platforms.add("instagram") }
        postInstaStory.value?.let { it -> if(it) platforms.add("insta_story") }

        val post = Post(
            getUid(),
            title.value,
            text.value,
            postId,
            downloadUri,
            date = date.toString("dd"),
            month = date.toString("MM"),
            year = date.toString("yyyy"),
            category = categorySelected.value,
            media = platforms
        )

        firebaseInteractor.writeNewPost(tags.value, post)
        _dataLoading.value = false

    }

     fun writePostPending(date: Date) {
         datePending.value = date
         pendingProcess.value = true

         if(!uriPhoto.value.isNullOrBlank()) {
             uploadFirebaseImage()
         }
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
        return getEncrypSharedPreferences(context).getString(
            SecurityConstants.UID_FIREBASE,
            null
        )
    }

    fun resetAllFields() {
        text.value = ""
        title.value = ""
        tags.value = ""
        postFacebook.value = false
        postInsta.value = false
        postInstaStory.value = false
        downloadPhotoUrl.value = null
        postPendingDate.value = null
        postPending.value = null

    }

    override fun onCleared() {
        super.onCleared()
        smmsCompositeDisposable.dispose()
    }

    fun savePendingPost() {
        var platforms: MutableList<String> = arrayListOf()

        postFacebook.value?.let { it -> if(it) platforms.add("facebook") }
        postInsta.value?.let { it -> if(it) platforms.add("instagram") }
        postInstaStory.value?.let { it -> if(it) platforms.add("insta_story") }

        val post = Post(
            getUid(),
            title.value,
            text.value,
            urlPicture = downloadPhotoUrl.value,
            date = datePending.value?.toString("dd"),
            month = datePending.value?.toString("MM"),
            year = datePending.value?.toString("yyyy"),
            category = categorySelected.value,
            media = platforms,
            pending = true
        )

        firebaseInteractor.writeNewPost(tags.value, post)
        _dataLoading.value = false
        resetAllFields()
    }

}
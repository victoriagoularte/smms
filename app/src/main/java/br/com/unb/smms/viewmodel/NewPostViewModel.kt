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
import br.com.unb.smms.domain.firebase.Tag
import br.com.unb.smms.extension.toString
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.firebase.database.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

class NewPostViewModel(val app: Application) : AndroidViewModel(app) {

    private val pageInteractor = PageInteractor(app.applicationContext)
    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var feedDisposable: Disposable

    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()

    val title = MutableLiveData<String>()
    val text = MutableLiveData<String>()
    val tags = MutableLiveData<String>()
    val textErrorMessage = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun feed(downloadUri: Uri?) {

        _dataLoading.value = true

        var tagsPost : String
        var textPost : String
        var feed : Feed? = null

        if(tags.value != null) {
            tagsPost = "#" + (tags.value)?.split(", ", ",")?.distinct()!!.joinToString(" #")
            textPost = text.value + "\n.\n.\n.\n.\n.\n" + tagsPost
            feed = Feed(textPost)
        } else {
            feed = Feed(text.value)
        }

        if (downloadUri != null) {
            feed.url = downloadUri.toString()
        }

        textErrorMessage.value = pageInteractor.validateTextPost(feed)
        if (!textErrorMessage.value.isNullOrEmpty()) return

        if (downloadUri == null) {
            feedDisposable = pageInteractor.feed(feed)
                .subscribe { res, _ ->
                    if (res != null) {
                        writeNewPost(res.id!!, downloadUri)
                        resultPost.value = SmmsData.Success(res)
                    }
                }

        } else {
            feedDisposable = pageInteractor.photo(feed)
                .subscribe { res, _ ->
                    if (res != null) {
                        writeNewPost(res.id!!, downloadUri.toString())
                        resultPost.value = SmmsData.Success(res)
                    }
                }
        }

        smmsCompositeDisposable.add(feedDisposable)
    }

    private fun writeNewPost(postId: String, downloadUri: String?) {

        var annotations: MutableList<Tag> = arrayListOf()
        val tags = (tags.value)?.split(", ", ",", " ")?.distinct()
        val database = FirebaseDatabase.getInstance().reference
        val newPostRef = database.child("posts")
        val newTagRef = database.child("tags")

        for (tag in tags!!) {

            annotations.add(Tag(tag))

            newTagRef.orderByChild("description").equalTo(tag)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        newTagRef.push().child("description").setValue(tag)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            newTagRef.push().child("description").setValue(tag)
                        }
                    }
                })
        }

        val currentDate = Calendar.getInstance().time

        val post = Post(getUid(), title.value, text.value, postId, downloadUri, currentDate.toString("MM/yyyy"), annotations)
        newPostRef.push().setValue(post)

        _dataLoading.value = false

    }

    private fun getUid(): String? {
        return getEncrypSharedPreferences(app.baseContext).getString(
            SecurityConstants.UID_FIREBASE,
            null
        )
    }




}
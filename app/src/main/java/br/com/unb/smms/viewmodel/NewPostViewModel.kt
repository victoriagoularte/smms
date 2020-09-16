package br.com.unb.smms.viewmodel

import android.content.Context
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.domain.firebase.Tag
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.firebase.database.*
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class NewPostViewModel @ViewModelInject constructor(val pageInteractor: PageInteractor, @ApplicationContext val context: Context) : ViewModel() {

    private lateinit var database: DatabaseReference

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var feedDisposable: Disposable

    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()

    val text = MutableLiveData<String>()
    val tags = MutableLiveData<String>()
    val textErrorMessage = MutableLiveData<String>()

    fun feed(downloadUri: Uri?) {

        var tagsPost : String
        var textPost : String
        var feed : Feed? = null

        if(tags.value != null) {
            tagsPost = "${ "#" + (tags.value)?.split(", ", ",")?.distinct()!!.joinToString(" #")}"
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

        var annotations : MutableList<Tag> = arrayListOf()
        val tags = (tags.value)?.split(", " , ",")?.distinct()
        val database = FirebaseDatabase.getInstance().reference
        val newPostRef = database.child("posts")
        val newTagRef = database.child("tags")

        for(tag in tags!!) {

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

        val post = Post(getUid(), "teste", text.value, downloadUri, annotations)

        newPostRef.push().setValue(post)



    }

    private fun getUid(): String? {
        return getEncrypSharedPreferences(context).getString(
            SecurityConstants.UID_FIREBASE,
            null
        )
    }




}
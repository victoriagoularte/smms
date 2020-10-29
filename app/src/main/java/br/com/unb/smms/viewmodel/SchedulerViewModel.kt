package br.com.unb.smms.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.interactor.FirebaseInteractor
import br.com.unb.smms.interactor.PageInteractor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class SchedulerViewModel @ViewModelInject constructor(
    val firebaseInteractor: FirebaseInteractor,
    val pageInteractor: PageInteractor
) :
    ViewModel() {

    var posts = MutableLiveData<SmmsData<List<Post>>>()
    var resultPost = MutableLiveData<SmmsData<NodeGraph>>()
    var resultUpdate = MutableLiveData<SmmsData<Boolean>>()

    private lateinit var postDisposable: Disposable
    private lateinit var feedDisposable: Disposable
    private lateinit var updateDisposable: Disposable
    private val composite = CompositeDisposable()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun getPosts() {

        _dataLoading.value = true

        postDisposable = firebaseInteractor.getPendingPosts().subscribe { res, error ->

            _dataLoading.value = false

            if(error != null) {
                posts.value = SmmsData.Error(error)
                return@subscribe
            }

            posts.value = SmmsData.Success(res)

        }

        composite.add(postDisposable)

//      firebaseInteractor.getPendingPosts().subscribe { res, error ->
//            if(error != null) {
//                posts.value =  SmmsData.Error(error)
//                return@subscribe
//            }
//
//            if(res.isNotEmpty()) {
//                posts.value = SmmsData.Success(res)
//            }
//        }

    }

    fun updatePostPending(post: Post) {

        updateDisposable = firebaseInteractor.updatePostPendingToPublish(post).subscribe { res, error ->
            if(error != null) {
                resultUpdate.value = SmmsData.Error(error)
                return@subscribe
            }

            resultUpdate.value = SmmsData.Success(res)

        }

        composite.add(updateDisposable)
    }

    fun postPublishPending(post: Post) {

        var textPost = ""

        post.body?.let {
            textPost = post.body!!
        }

        post.annotations?.let {
            val tagsPost = (post.annotations)?.joinToString(separator = " #")
            textPost = post.body + "\n.\n.\n.\n.\n.\n" + tagsPost
        }

        if (post.urlPicture == null) {
            feedDisposable = pageInteractor.feed(Feed(textPost))
                .subscribe { res, _ ->
                    if (res != null) {
                        updatePostPending(post)
                        resultPost.value = SmmsData.Success(res)
                    }
                }

        } else {
            feedDisposable = pageInteractor.photo(Feed(textPost, post.urlPicture))
                .subscribe { res, _ ->
                    if (res != null) {
                        updatePostPending(post)
                        resultPost.value = SmmsData.Success(res)
                    }
                }
        }

        composite.add(feedDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        composite.clear()
    }

}
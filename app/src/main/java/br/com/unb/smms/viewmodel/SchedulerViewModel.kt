package br.com.unb.smms.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.interactor.FirebaseInteractor
import io.reactivex.disposables.Disposable

class SchedulerViewModel @ViewModelInject constructor(val firebaseInteractor: FirebaseInteractor) :
    ViewModel() {

    var posts = MutableLiveData<SmmsData<List<Post>>>()

    private lateinit var postDisposable: Disposable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun getPosts() {

        _dataLoading.value = true

        postDisposable = firebaseInteractor.getPendingPosts()
            .doOnNext {
                posts.value = SmmsData.Success(it)
            }.doOnError {
                posts.value = SmmsData.Error(it)
            }.doOnSubscribe {
                _dataLoading.value = false
            }
            .subscribe()
    }

    fun updatePostPending() {

    }

}
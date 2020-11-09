package br.com.unb.smms.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.interactor.FirebaseInteractor
import io.reactivex.disposables.Disposable

class SearchViewModel @ViewModelInject constructor(
    private val firebaseInteractor: FirebaseInteractor
) : ViewModel() {

    var title = MutableLiveData<String>()
    var post = MutableLiveData<String>()
    val annotation = MutableLiveData<String>()

    private val _showList = MutableLiveData<Boolean>()
    val showList: LiveData<Boolean>
        get() = _showList

    var resultSearch = MutableLiveData<SmmsData<List<Post>>>()

    private lateinit var disposable: Disposable

    fun search() {

        _showList.value = true

        disposable = firebaseInteractor.search(title.value, post.value, annotation.value)
            .subscribe { res, error ->

                _showList.value = false

                if (error != null) {
                    resultSearch.value = SmmsData.Error(error)
                    return@subscribe
            }

            resultSearch.value = SmmsData.Success(res.toList())
        }
    }



}
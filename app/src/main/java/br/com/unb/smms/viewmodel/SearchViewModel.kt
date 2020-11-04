package br.com.unb.smms.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.interactor.FirebaseInteractor
import br.com.unb.smms.interactor.PageInteractor
import io.reactivex.disposables.Disposable

class SearchViewModel @ViewModelInject constructor(
    private val firebaseInteractor: FirebaseInteractor
) : ViewModel() {

    var title = MutableLiveData("")
    var post = MutableLiveData("")
    val annotation = MutableLiveData("")

    var resultSearch = MutableLiveData<SmmsData<List<Post>>>()

    private lateinit var disposable: Disposable

    fun search() {
        disposable = firebaseInteractor.search(title.value ?: "", post.value ?: "", annotation.value ?: "").subscribe { res, error ->
            if(error != null) {
                resultSearch.value = SmmsData.Error(error)
                return@subscribe
            }

            resultSearch.value = SmmsData.Success(res as List<Post>)
        }
    }



}
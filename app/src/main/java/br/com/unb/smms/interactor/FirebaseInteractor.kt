package br.com.unb.smms.interactor

import android.net.Uri
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.repository.FirebaseRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class FirebaseInteractor @Inject constructor(private val firebaseRepository: FirebaseRepository) {

    fun writeNewPost(tags: String?, post: Post) {
        firebaseRepository.writeNewPost(tags, post)
    }

    fun uploadImageFirebase(imagePath: String) : Single<Uri> {
        return firebaseRepository.uploadImageFirebase(imagePath)
    }

    fun getPendingPosts() : Observable<List<Post>> {
        return firebaseRepository.getPendingPosts()
    }



}
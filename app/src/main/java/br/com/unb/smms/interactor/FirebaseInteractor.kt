package br.com.unb.smms.interactor

import android.content.Context
import android.net.Uri
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.repository.FirebaseRepository
import io.reactivex.Single

class FirebaseInteractor(val context: Context) {

    private val firebaseRepository = FirebaseRepository()

    fun writeNewPost(tags: String?, post: Post) {
        firebaseRepository.writeNewPost(tags, post)
    }

    fun uploadImageFirebase(imagePath: String) : Single<Uri> {
        return firebaseRepository.uploadImageFirebase(imagePath)
    }

}
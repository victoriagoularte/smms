package br.com.unb.smms.interactor

import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.repository.FirebaseRepository
import javax.inject.Inject

class FirebaseInteractor @Inject constructor(private val firebaseRepository: FirebaseRepository) {

    fun writeNewPost(tags: String?, post: Post) = firebaseRepository.writeNewPost(tags, post)
    fun uploadImageFirebase(imagePath: String) = firebaseRepository.uploadImageFirebase(imagePath)
    fun getPendingPosts() = firebaseRepository.getPendingPosts()
    fun updatePostPendingToPublish(id: String, post: Post) = firebaseRepository.updatePostPending(id, post)


}
package br.com.unb.smms.interactor

import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.repository.FirebaseRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import retrofit2.http.Body
import javax.inject.Inject

class FirebaseInteractor @Inject constructor(private val firebaseRepository: FirebaseRepository) {

    fun writeNewPost(tags: String?, post: Post) = firebaseRepository.writeNewPost(tags, post)
    fun uploadImageFirebase(imagePath: String) = firebaseRepository.uploadImageFirebase(imagePath)
    fun getPendingPosts() = firebaseRepository.getPendingPosts()
    fun updatePostPendingToPublish(post: Post) = firebaseRepository.updatePostPending(post)
    fun findByTitle(title: String) = firebaseRepository.findByTitle(title)
    fun findByAnnotation(annotation: String) = firebaseRepository.findByAnnotation(annotation)
    fun findByPost(post: String) = firebaseRepository.findByBody(post)
    fun search(title: String, post: String, annotation: String): Single<Set<Post>> {
        return Single.zip(firebaseRepository.findByTitle(title), firebaseRepository.findByBody(post), firebaseRepository.findByAnnotation(annotation), Function3 {
                byTitle: List<Post>, byPost: List<Post>, byAnnotation: List<Post> ->
                byTitle.intersect(byPost).intersect(byAnnotation)
        })
    }


}
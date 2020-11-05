package br.com.unb.smms.interactor

import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.repository.FirebaseRepository
import io.reactivex.Single
import javax.inject.Inject

class FirebaseInteractor @Inject constructor(private val firebaseRepository: FirebaseRepository) {

    fun writeNewPost(tags: String?, post: Post) = firebaseRepository.writeNewPost(tags, post)
    fun uploadImageFirebase(imagePath: String) = firebaseRepository.uploadImageFirebase(imagePath)
    fun getPendingPosts() = firebaseRepository.getPendingPosts()
    fun updatePostPendingToPublish(post: Post) = firebaseRepository.updatePostPending(post)
    fun search(title: String?, post: String?, annotation: String?): Single<List<Post>> {

        val listTitle = if (title.isNullOrEmpty()) {
            Single.create { it.onSuccess(emptyList()) }
        } else {
            firebaseRepository.findByTitle(title)
        }

        val listAnnotation = if (annotation.isNullOrEmpty()) {
            Single.create { it.onSuccess(emptyList()) }
        } else {
            firebaseRepository.findByAnnotation(annotation)
        }

        val listPost = if (post.isNullOrEmpty()) {
            Single.create { it.onSuccess(emptyList()) }
        } else {
            firebaseRepository.findByBody(post)
        }

        return Single.zip(listTitle,
            listPost,
            listAnnotation,
            { byTitle: List<Post>, byPost: List<Post>, byAnnotation: List<Post> ->
                when {
                    byTitle.isNullOrEmpty() -> {
                        if (byAnnotation.isNullOrEmpty()) {
                            byPost
                        } else if (byPost.isNullOrEmpty()) {
                            byAnnotation
                        } else {
                            byPost.intersect(byAnnotation).toList()
                        }
                    }
                    byPost.isNullOrEmpty() -> {
                        if (byAnnotation.isNullOrEmpty()) {
                            byTitle
                        } else if (byTitle.isNullOrEmpty()) {
                            byAnnotation
                        } else {
                            byAnnotation.intersect(byTitle).toList()
                        }
                    }
                    byAnnotation.isNullOrEmpty() -> {
                        if (byTitle.isNullOrEmpty()) {
                            byPost
                        } else if (byPost.isNullOrEmpty()) {
                            byTitle
                        } else {
                            byPost.intersect(byTitle).toList()
                        }
                    }
                    else -> {
                        byTitle.intersect(byPost).intersect(byAnnotation).toList()
                    }
                }

            })
    }


}
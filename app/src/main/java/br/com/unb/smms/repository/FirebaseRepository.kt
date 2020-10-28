package br.com.unb.smms.repository

import android.net.Uri
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.domain.firebase.Tag
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File


class FirebaseRepository {

    private var mStorageRef: StorageReference = FirebaseStorage.getInstance().reference

    fun writeNewPost(tags: String?, post: Post) {

        var annotations: MutableList<Tag> = arrayListOf()
        val tags = (tags)?.split(", ", ",", " ")?.distinct()
        val database = FirebaseDatabase.getInstance().reference
        val newPostRef = database.child("posts")
        val newTagRef = database.child("tags")

        if(tags != null) {
            for (tag in tags) {

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
        }

        post.annotations = annotations
        newPostRef.push().setValue(post)
    }

    fun uploadImageFirebase(imagePath: String): Single<Uri> {

        val file = Uri.fromFile(File(imagePath))
        val ref: StorageReference = mStorageRef.child("images/${file.lastPathSegment}")
        var uploadTask = ref.putFile(file)

        return Single.create { uri ->
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    uri.onError(Throwable(task.exception))
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uri.onSuccess(task.result!!)
                }
            }

        }
    }

    fun getPendingPosts(): Observable<List<Post>> {

        val database = FirebaseDatabase.getInstance().reference
        val postRef = database.child("posts")
        val pendingPosts = postRef.child("pending")

        return Observable.create {

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listPost: MutableList<Post> = arrayListOf()
                    var snapshotIterable = snapshot.children
                    var iterator = snapshotIterable.iterator()

                    while (iterator.hasNext()) {
                        val post = iterator.next().getValue(Post::class.java)
                        if (post != null && post.pending) {
                            listPost.add(post)
                        }
                    }

                    it.onNext(listPost);
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(FirebaseException(error.message))
                }

            })

        }
    }

    fun updatePostPending(id: String, post: Post): Single<Boolean> {
        val database = FirebaseDatabase.getInstance().reference
        val postRef = database.child("posts")

        post.pending = false

        val childUpdates = hashMapOf<String, Any>(
            "/posts/$id" to post
        )

        return Single.create { emitter ->
            postRef.updateChildren(childUpdates).addOnSuccessListener {
                emitter.onSuccess(true)
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }
}
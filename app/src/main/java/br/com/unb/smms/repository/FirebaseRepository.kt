package br.com.unb.smms.repository

import android.net.Uri
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.domain.firebase.Tag
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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

    fun getPendingPosts(): Single<List<Post>> {

        val database = FirebaseDatabase.getInstance().reference
        val postRef = database.child("posts")

        return Single.create {

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listPost: MutableList<Post> = arrayListOf()
                    var snapshotIterable = snapshot.children
                    var iterator = snapshotIterable.iterator()

                    while (iterator.hasNext()) {
                        val teste = iterator.next()
                        val post = teste.getValue(Post::class.java)
                        post?.id = teste.key
                        if (post != null && post.pending) {
                            listPost.add(post)
                        }
                    }
                    it.onSuccess(listPost)
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(Throwable("error: lista de posts pendentes nao carregada"))
                }
            })
        }
    }

    fun updatePostPending(post: Post): Single<Boolean> {
        val database = FirebaseDatabase.getInstance().reference
        post.pending = false

        val childUpdates = hashMapOf<String, Any>(
            "/posts/${post.id}" to post
        )

        return Single.create { emitter ->
            database.updateChildren(childUpdates).addOnSuccessListener {
                emitter.onSuccess(true)
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

    fun findByAnnotation(annotation: String): Single<List<Post>> {

        val database = FirebaseDatabase.getInstance().reference
        val postRef = database.child("posts")

        return Single.create {

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var listPost: MutableList<Post> = arrayListOf()
                    var snapshotIterable = snapshot.children
                    var iterator = snapshotIterable.iterator()

                    while (iterator.hasNext()) {
                        val teste = iterator.next()
                        val post = teste.getValue(Post::class.java)
                        post?.id = teste.key
                        if (post != null && !post.annotations.isNullOrEmpty()) {
                            for (tag in post.annotations!!) {
                                if (tag.description == annotation) {
                                    listPost.add(post)
                                }
                            }
                        }
                    }

                    it.onSuccess(listPost)
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(Throwable("error: nenhum post encontrado"))
                }
            })
        }
    }

    fun findByTitle(title: String): Single<List<Post>> {

        val database = FirebaseDatabase.getInstance().reference
        val titleRef = database.child("posts/title")

        return Single.create {

            titleRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var listPost: MutableList<Post> = arrayListOf()
                    var snapshotIterable = snapshot.children
                    var iterator = snapshotIterable.iterator()

                    while (iterator.hasNext()) {
                        val teste = iterator.next()
                        val post = teste.getValue(Post::class.java)
                        post?.id = teste.key
                        if (post != null && !post.title.isNullOrEmpty()) {
                            if (post.title!!.contains(title, true)) {
                                listPost.add(post)
                            }
                        }
                    }

                    it.onSuccess(listPost)
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(Throwable("error: nenhum post encontrado"))
                }
            })
        }
    }

    fun findByBody(body: String): Single<List<Post>> {

        val database = FirebaseDatabase.getInstance().reference
        val descriptionRef = database.child("posts/body")

        return Single.create {

            descriptionRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var listPost: MutableList<Post> = arrayListOf()
                    var snapshotIterable = snapshot.children
                    var iterator = snapshotIterable.iterator()

                    while (iterator.hasNext()) {
                        val teste = iterator.next()
                        val post = teste.getValue(Post::class.java)
                        post?.id = teste.key
                        if (post != null && !post.body.isNullOrEmpty()) {
                            if (post.body?.contains(body, true)!!) {
                                listPost.add(post)
                            }
                        }
                    }

                    it.onSuccess(listPost)
                }

                override fun onCancelled(error: DatabaseError) {
                    it.onError(Throwable("error: nenhum post encontrado"))
                }
            })
        }
    }
}
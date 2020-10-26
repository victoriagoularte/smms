package br.com.unb.smms.repository

import android.net.Uri
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.domain.firebase.Tag
import com.google.firebase.database.*
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

    fun getPublishesScheduler() {

        var posts: MutableList<Post> = arrayListOf()

        val database = FirebaseDatabase.getInstance().reference
        val postRef = database.child("posts")
        val pendingPosts = postRef.child("pending")

        pendingPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)!!
                posts.add(post)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }


}
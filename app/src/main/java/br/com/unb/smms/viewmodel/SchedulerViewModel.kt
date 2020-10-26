package br.com.unb.smms.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.interactor.FirebaseInteractor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SchedulerViewModel @ViewModelInject constructor(val firebaseInteractor: FirebaseInteractor) :
    ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    fun getPosts() : List<Post> {

        _dataLoading.value = true

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

        return posts
    }




}
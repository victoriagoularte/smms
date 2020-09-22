package br.com.unb.smms.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.domain.facebook.ListPost
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.extension.toString
import br.com.unb.smms.interactor.FirebaseInteractor
import br.com.unb.smms.interactor.IgInteractor
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.interactor.UserInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

class AnalitycsViewModel @ViewModelInject constructor(private val userInteractor: UserInteractor, private val pageInteractor: PageInteractor, val firebaseInteractor: FirebaseInteractor,
                                                      private val igInteractor: IgInteractor, @ApplicationContext val context: Context) : ViewModel() {

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var friendsFacebookDisposable: Disposable
    private lateinit var instaInfoDisposable: Disposable
    private lateinit var instaUserDisposable: Disposable
    private lateinit var postsDisposable: Disposable

    var resultFacebookFriends = MutableLiveData<String>()
    var followersCount = MutableLiveData<String>()

    var resultUserIdIg = MutableLiveData<SmmsData<NodeGraph>>()
    var resultFacebookPosts = MutableLiveData<SmmsData<ListPost>>()
    var resultInstaInfo = MutableLiveData<SmmsData<IgInfo>>()
    var resultPosts = MutableLiveData<List<Post>>()
    var periodSelected = MutableLiveData<String>()
    var facebookChecked = MutableLiveData<Boolean>()
    var instagramChecked = MutableLiveData<Boolean>()

    fun getFriendsCount() {

        friendsFacebookDisposable = userInteractor.getFriendsCount()
            .subscribe { res, error ->
                if (res?.summary != null)
                    resultFacebookFriends.value = (res.summary!!.totalCount).toString()
            }

        smmsCompositeDisposable.add(friendsFacebookDisposable)

    }

    fun postsFacebook() {

        postsDisposable = pageInteractor.postsFacebook().subscribe { res, error ->

            res?.let {
                resultFacebookPosts.value = SmmsData.Success(res)
                return@subscribe
            }

            error?.let {
                resultFacebookPosts.value = SmmsData.Error(error)
                return@subscribe
            }

        }

    }

    fun userIdIg() {

        instaUserDisposable = pageInteractor.igBusinessAccount()
            .subscribe { res, error ->
                if(error != null) {
                    resultUserIdIg.value = SmmsData.Error(error)
                    return@subscribe
                }

                getEncrypSharedPreferences(context).edit()
                    .putString(SecurityConstants.IG_BUSINESS_ACCOUNT, res!!.igBusinessAccount!!.id).apply()

                resultUserIdIg.value = SmmsData.Success(res.igBusinessAccount!!)
                infoIg()
            }

        smmsCompositeDisposable.add(instaUserDisposable)


    }

    fun infoIg() {

        instaInfoDisposable = igInteractor.igInfo()
            .subscribe { res, error ->
                if(error != null) {
                    resultInstaInfo.value = SmmsData.Error(error)
                    return@subscribe
                }

                followersCount.value = res.followersCount.toString()

            }

        smmsCompositeDisposable.add(instaInfoDisposable)

    }

    fun getPostsByPeriod(period: String?) {

        val period = period ?: "day"

        var posts: MutableList<Post> = arrayListOf()
        val database = FirebaseDatabase.getInstance().reference
        val postsRef = database.child("posts")

        val currentDay = Calendar.getInstance().time.toString("dd")
        val currentMonth = Calendar.getInstance().time.toString("MM")
        val currentYear = Calendar.getInstance().time.toString("yyyy")

        postsRef.orderByChild("month").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)!!
                    if (period == "day") {
                        if (post.date == currentDay && post.month == currentMonth && post.year == currentYear) {
                            posts.add(post)
                            resultPosts.value = posts
                        }
                    } else if (period == "month") {
                        if (post.month == currentMonth && post.year == currentYear) {
                            posts.add(post)
                            resultPosts.value = posts
                        }
                    } else {
                        if (post.year == currentYear) {
                            posts.add(post)
                            resultPosts.value = posts
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//        return posts

    }



    override fun onCleared() {
        super.onCleared()
        smmsCompositeDisposable.dispose()
    }
}
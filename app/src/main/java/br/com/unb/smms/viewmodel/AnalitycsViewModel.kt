package br.com.unb.smms.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.unb.smms.SmmsData
import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.domain.facebook.ListPost
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.domain.facebook.PostFacebook
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.extension.toString
import br.com.unb.smms.interactor.FirebaseInteractor
import br.com.unb.smms.interactor.IgInteractor
import br.com.unb.smms.interactor.PageInteractor
import br.com.unb.smms.interactor.UserInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.github.mikephil.charting.data.Entry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList

class AnalitycsViewModel @ViewModelInject constructor(private val userInteractor: UserInteractor, private val pageInteractor: PageInteractor, val firebaseInteractor: FirebaseInteractor,
                                                      private val igInteractor: IgInteractor, @ApplicationContext val context: Context) : ViewModel() {

    private val smmsCompositeDisposable = CompositeDisposable()
    private lateinit var facebookFollowers: Disposable
    private lateinit var instaInfoDisposable: Disposable
    private lateinit var instaUserDisposable: Disposable
    private lateinit var postsDisposable: Disposable
    private lateinit var likesDisposable: Disposable
    private lateinit var commentsDisposable: Disposable

    var resultFacebookFollowers = MutableLiveData<String>()
    var followersCount = MutableLiveData<String>()

    var resultUserIdIg = MutableLiveData<SmmsData<NodeGraph>>()
    var resultFacebookPosts = MutableLiveData<SmmsData<ListPost>>()
    var resultInstaInfo = MutableLiveData<SmmsData<IgInfo>>()

    var resultCountLikes = MutableLiveData(0)
    var resultCountImpressions = MutableLiveData(0)
    var resultCountComments = MutableLiveData(0)

    var resultPosts = MutableLiveData<List<Post>>()
    var periodSelected = MutableLiveData<String>()
    var facebookChecked = MutableLiveData<Boolean>()
    var instagramChecked = MutableLiveData<Boolean>()

    var entriesLikes = MutableLiveData<ArrayList<Entry>>()
    var entriesComments = MutableLiveData<ArrayList<Entry>>()

    private val _showLikes = MutableLiveData<Boolean>()
    val showLikes: LiveData<Boolean>
        get() = _showLikes

    private val _showComments = MutableLiveData<Boolean>()
    val showComments: LiveData<Boolean>
        get() = _showComments

    private val _showImpressions = MutableLiveData<Boolean>()
    val showImpressions: LiveData<Boolean>
        get() = _showImpressions

    fun followers() {

        facebookFollowers = pageInteractor.followers()
            .subscribe { res, error ->
                if (res != null)
                    resultFacebookFollowers.value = (res.fanCount).toString()
            }

        smmsCompositeDisposable.add(facebookFollowers)

    }

    fun postsFacebook() {

        _showLikes.value = false
        _showImpressions.value = false
        _showComments.value = false

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

    fun filterPostsByPeriod(period: String, list: List<PostFacebook>): List<PostFacebook> {
        return list.filter {
            when (period) {
                "day" -> {
                    var nextDate = Date()
                    val cal = Calendar.getInstance()
                    cal.time = nextDate
                    cal.add(Calendar.DATE, 0)
                    nextDate = cal.time

                    (it.created_time?.substring(8, 10) == cal.time.toString("dd") ||
                            it.created_time?.substring(8, 10) == nextDate.toString("dd")) &&
                            it.created_time?.substring(5, 7) == Calendar.getInstance().time.toString("MM")
                }
                "month" -> {
                    it.created_time?.substring(5, 7) == Calendar.getInstance().time.toString("MM")
                }
                else -> {
                    it.created_time?.substring(0, 4) == Calendar.getInstance().time.toString("yyyy")
                }
            }
        }
    }

    fun postInsightLikes(ids: List<String>) {

        likesDisposable = pageInteractor.postInsights(ids, "likes").subscribe { res, error ->

            res?.let {
                val entrylist = ArrayList<Entry>()
                resultCountLikes.value =  it.sum()
                for(i in it.indices) {
                    entrylist.add(Entry(i.toFloat(), it[i].toFloat()))
                }
                entriesLikes.value = entrylist
                _showLikes.value = true

            }

            error?.let {
                resultCountLikes.value = 0
                _showLikes.value = true
            }
        }

        smmsCompositeDisposable.add(likesDisposable)
    }


    fun postInsightImpressions(ids: List<String>) {
//        postInsights(ids, "seen")
    }

    fun postInsightComments(ids: List<String>) {

        commentsDisposable = pageInteractor.postInsights(ids, "comments").subscribe { res, error ->

            res?.let {
                val entrylist = ArrayList<Entry>()
                resultCountComments.value =  it.sum()
                for(i in it.indices) {
                    entrylist.add(Entry(i.toFloat(), it[i].toFloat()))
                }
                entriesComments.value = entrylist
                _showComments.value = true

            }

            error?.let {
                resultCountComments.value = 0
                _showComments.value = true
            }
        }

        smmsCompositeDisposable.add(commentsDisposable)
    }

    fun userIdIg() {

        instaUserDisposable = pageInteractor.igBusinessAccount()
            .subscribe { res, error ->
                if (error != null) {
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
    }

    override fun onCleared() {
        super.onCleared()

        resultCountLikes.value = 0
        resultCountImpressions.value = 0
        resultCountComments.value = 0

        smmsCompositeDisposable.dispose()
    }
}
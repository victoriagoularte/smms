package br.com.unb.smms.interactor

import android.content.Context
import android.graphics.Bitmap
import br.com.unb.smms.R
import br.com.unb.smms.domain.facebook.*
import br.com.unb.smms.repository.PageRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.gson.Gson
import io.reactivex.Single
import javax.inject.Inject

class PageInteractor @Inject constructor(private val smmsRepository: PageRepository, val context: Context) {

    fun validateTextPost(feed: Feed): String? {
        return if(feed.message.isNullOrEmpty()) {
            context.getString(R.string.text_invalid)
        } else null
    }

    fun feed(feed: Feed): Single<NodeGraph?> {
        return smmsRepository.feed(getIdAccount(), feed)
    }

    fun profilePicture(): Single<Bitmap> {
        return smmsRepository.profilePicture(getIdAccount())
    }

    fun postInsights(ids: List<String>, metric: String): Single<List<Int>> {
        return smmsRepository.postInsights(ids, metric)
    }

    fun postsFacebook(): Single<ListPost?> {
        return smmsRepository.posts(getIdAccount())
    }

    fun photo(feed: Feed): Single<NodeGraph?> {
        return smmsRepository.photo(getIdAccount(), feed)
    }

    fun igBusinessAccount(): Single<IgBusinessAccount?> {
        return smmsRepository.igBusinessAccount(getIdAccount())
    }

    private fun getIdAccount(): String {
        val gson = Gson()
        val loginResultString = getEncrypSharedPreferences(context).getString(
            SecurityConstants.PAGE_INFO,
            ""
        )

        val account = gson.fromJson(loginResultString, Account::class.java)
        return if (account?.id != null) account.id!! else "109187477422515"
    }




}
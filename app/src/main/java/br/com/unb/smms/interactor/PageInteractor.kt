package br.com.unb.smms.interactor

import android.content.Context
import br.com.unb.smms.R
import br.com.unb.smms.domain.*
import br.com.unb.smms.repository.PageRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.gson.Gson
import io.reactivex.Single

class PageInteractor(val context: Context) {

    private val accessToken = getPageAccessToken()

    private val smmsRepository =
        PageRepository(context, context.getString(R.string.facebook_server), accessToken!!.accessToken)

    fun validateTextPost(feed: Feed): String? {
        return if(feed.message.isNullOrEmpty()) {
            context.getString(R.string.text_invalid)
        } else null
    }

    fun feed(feed: Feed): Single<NodeGraph?> {
        return smmsRepository.feed(accessToken!!.id!!, feed)
    }

    fun photo(id: String, feed: Feed): Single<NodeGraph?> {
        return smmsRepository.photo(accessToken!!.id!!, feed)
    }

    fun igBusinessAccount(): Single<IgBusinessAccount?> {
        return smmsRepository.igBusinessAccount(accessToken!!.id!!)
    }

    private fun getPageAccessToken(): Account? {
        val gson = Gson()
        val loginResultString = getEncrypSharedPreferences(context).getString(
            SecurityConstants.PAGE_INFO,
            ""
        )

        return gson.fromJson(loginResultString, Account::class.java)

    }



}
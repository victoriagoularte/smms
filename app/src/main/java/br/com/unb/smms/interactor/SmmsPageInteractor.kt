package br.com.unb.smms.interactor

import android.content.Context
import br.com.unb.smms.R
import br.com.unb.smms.domain.Account
import br.com.unb.smms.domain.Feed
import br.com.unb.smms.domain.NodeGraph
import br.com.unb.smms.repository.SmmsPageRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.gson.Gson
import io.reactivex.Single

class SmmsPageInteractor(val context: Context) {

    private val accessToken = getPageAccessToken()

    private val smmsRepository =
        SmmsPageRepository(context, context.getString(R.string.facebook_server), accessToken)

    fun validateTextPost(feed: Feed): String? {
        return if(feed.message.isNullOrEmpty()) {
            context.getString(R.string.text_invalid)
        } else null
    }

    fun feed(id: String, feed: Feed): Single<NodeGraph?> {
        return smmsRepository.feed(id, feed)
    }

    private fun getPageAccessToken() : String? {
        val gson = Gson()
        val loginResultString = getEncrypSharedPreferences(context).getString(
            SecurityConstants.PAGE_INFO,
            ""
        )

        val result = gson.fromJson(loginResultString, Account::class.java)
        return result.accessToken
    }



}
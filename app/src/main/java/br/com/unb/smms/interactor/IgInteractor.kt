package br.com.unb.smms.interactor

import android.content.Context
import br.com.unb.smms.R
import br.com.unb.smms.domain.IgInfo
import br.com.unb.smms.repository.IgRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.gson.Gson
import io.reactivex.Single

class IgInteractor(val context: Context) {

    private val accessToken = getUserAccessToken();
    private val idUserIg = getIdUserIg();

    private val igRepository =
        IgRepository(
            context,
            context.getString(R.string.facebook_server),
            accessToken.token
        )

    fun igInfo() : Single<IgInfo> {
        return igRepository.igInfo(idUserIg!!)
    }



    private fun getUserAccessToken(): AccessToken {
        val gson = Gson()
        val loginResultString = getEncrypSharedPreferences(context).getString(
            SecurityConstants.LOGIN_RESULT,
            ""
        )

        val loginResult = gson.fromJson(loginResultString, LoginResult::class.java)
        return loginResult.accessToken
    }

    private fun getIdUserIg(): String? {
        val gson = Gson()
        return getEncrypSharedPreferences(context).getString(
            SecurityConstants.IG_BUSINESS_ACCOUNT,
            ""
        )

    }



}

package br.com.unb.smms.interactor

import android.content.Context
import android.graphics.Bitmap
import br.com.unb.smms.R
import br.com.unb.smms.domain.Account
import br.com.unb.smms.domain.Friends
import br.com.unb.smms.repository.UserRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.gson.Gson
import io.reactivex.Single

class UserInteractor(val context: Context) {

    private val accessToken = getUserAccessToken();

    private val smmsRepository =
        UserRepository(context, context.getString(R.string.facebook_server), accessToken.token)

    fun access(): Single<Account> {
        return smmsRepository.access(accessToken.userId)
    }

    fun getUserProfilePicture(): Single<Bitmap> {
        return smmsRepository.userProfilePicture(accessToken.userId)
    }

    fun getFriendsCount(): Single<Friends> {
        return smmsRepository.friends(accessToken.userId)
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



}

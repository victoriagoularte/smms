package br.com.unb.smms.interactor

import android.content.Context
import android.graphics.Bitmap
import br.com.unb.smms.R
import br.com.unb.smms.domain.Account
import br.com.unb.smms.domain.Friends
import br.com.unb.smms.domain.InstagramInfo
import br.com.unb.smms.repository.SmmsUserRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import io.reactivex.Single

class SmmsUserInteractor(val context: Context) {

    private val sharePref = getEncrypSharedPreferences(context)
    private val accessToken = sharePref.getString(SecurityConstants.ACCESS_TOKEN, "")

    private val smmsRepository =
        SmmsUserRepository(context, context.getString(R.string.facebook_server), accessToken)

    fun access(id: String): Single<Account> {
        return smmsRepository.access(id)
    }

    fun getUserProfilePicture(id: String): Single<Bitmap> {
        return smmsRepository.userProfilePicture(id)
    }

    fun getFriendsCount(id: String): Single<Friends> {
        return smmsRepository.friends(id)
    }

    fun infoIg(id: String): Single<InstagramInfo> {
        return smmsRepository.infoIg(id)
    }

}

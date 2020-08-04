package br.com.unb.smms.interactor

import android.content.Context
import br.com.unb.smms.R
import br.com.unb.smms.domain.facebook.Account
import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.repository.IgRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.gson.Gson
import io.reactivex.Single

class IgInteractor(val context: Context, val accessToken: String) {

    private val idUserIg = getIdUserIg();

    private val igRepository =
        IgRepository(
            context,
            context.getString(R.string.facebook_server),
            accessToken
        )

    fun igInfo() : Single<IgInfo> {
        return igRepository.igInfo(idUserIg!!)
    }

    private fun getIdUserIg(): String? {
        val gson = Gson()
        return getEncrypSharedPreferences(context).getString(
            SecurityConstants.IG_BUSINESS_ACCOUNT,
            ""
        )

    }



}

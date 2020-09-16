package br.com.unb.smms.interactor

import android.content.Context
import br.com.unb.smms.domain.facebook.Account
import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.repository.IgRepository
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Single
import javax.inject.Inject

class IgInteractor @Inject constructor(val igRepository: IgRepository, @ApplicationContext val context: Context) {

    private val accessToken = getPageAccessToken();
    private val idUserIg = getIdUserIg();

    fun igInfo() : Single<IgInfo> {
        return igRepository.igInfo(idUserIg!!)
    }



    private fun getPageAccessToken(): Account? {
        val gson = Gson()
        val loginResultString = getEncrypSharedPreferences(context).getString(
            SecurityConstants.PAGE_INFO,
            ""
        )

        return gson.fromJson(loginResultString, Account::class.java)

    }

    private fun getIdUserIg(): String? {
        val gson = Gson()
        return getEncrypSharedPreferences(context).getString(
            SecurityConstants.IG_BUSINESS_ACCOUNT,
            ""
        )

    }



}

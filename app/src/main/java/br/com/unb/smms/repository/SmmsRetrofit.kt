package br.com.unb.smms.repository

import android.content.Context
import br.com.unb.smms.BuildConfig
import br.com.unb.smms.domain.facebook.Account
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private class AddHeaderInterceptor(val context: Context, val type: Int) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val sharePref = getEncrypSharedPreferences(context)

        val accessToken = when (type) {
            0 -> getUserAccessToken(context).token
            else -> getPageAccessToken(context).accessToken
        }

        var builder = chain.request().newBuilder()
        if (accessToken != null) {
            val originalHttpUrl: HttpUrl = chain.request().url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("access_token", accessToken)
                .build()
            builder = builder.url(url)
        }

        return chain.proceed(builder.build())

    }

}

private fun getUserAccessToken(context: Context): AccessToken {
    val gson = Gson()
    val loginResultString = getEncrypSharedPreferences(context).getString(
        SecurityConstants.LOGIN_RESULT,
        ""
    )

    val loginResult = gson.fromJson(loginResultString, LoginResult::class.java)
    return loginResult.accessToken
}

private fun getPageAccessToken(context: Context): Account {
    val gson = Gson()
    val loginResultString = getEncrypSharedPreferences(context).getString(
        SecurityConstants.PAGE_INFO,
        ""
    )

    return gson.fromJson(loginResultString, Account::class.java)
}

open class SmmsRetrofit @Inject constructor(@ApplicationContext context: Context) {

    val retrofitUser: Retrofit
    val retrofitPage: Retrofit
    private val gson: Gson = GsonBuilder().create()

    init {

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val clientUser =
            OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(AddHeaderInterceptor(context, 0))
                .callTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()

        val clientPage =
            OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(AddHeaderInterceptor(context, 1))
                .callTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()

        retrofitUser = Retrofit.Builder()
            .client(clientUser)
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        retrofitPage = Retrofit.Builder()
            .client(clientPage)
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}
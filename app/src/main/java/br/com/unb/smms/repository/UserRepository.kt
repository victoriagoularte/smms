package br.com.unb.smms.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.unb.smms.domain.facebook.Account
import br.com.unb.smms.domain.facebook.Friends
import br.com.unb.smms.repository.dto.*
import br.com.unb.smms.repository.mapper.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.mapstruct.factory.Mappers
import retrofit2.http.*


interface FacebookService {

    @GET("me")
    fun pageInfo(): Single<NodeGraphDTO>

    @GET("{id}/accounts")
    fun access(@Path("id") userId: String): Single<AccessDTO>

    @GET("{id}/picture?height=200&width=200&type=normal")
    fun userProfilePicture(@Path("id") userId: String): Single<ResponseBody>

    @GET("{id}/friends")
    fun friends(@Path("id") userId: String): Single<FriendsDTO>

}


class UserRepository(val context: Context, val baseUrl: String, val accessToken: String?) : SmmsRetrofit(context, baseUrl, accessToken) {

    private val facebookService = retrofit.create(FacebookService::class.java)

    fun access(id: String): Single<Account> {

        val domainMapper = Mappers.getMapper(AccountMapper::class.java)

        return facebookService.access(id).map { accessDTO ->
            domainMapper.toDomain(accessDTO.data!![0])
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun userProfilePicture(id: String): Single<Bitmap> {
        return facebookService.userProfilePicture(id).map { it ->
            BitmapFactory.decodeByteArray(it.bytes(), 0, it.contentLength().toInt())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun friends(id: String): Single<Friends> {

        val domainMapper = Mappers.getMapper(FriendsResponseMapper::class.java)

        return facebookService.friends(id).map { friendsDTO ->
            domainMapper.toDomain(friendsDTO)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}
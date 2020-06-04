package br.com.unb.smms.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.unb.smms.domain.Account
import br.com.unb.smms.domain.Feed
import br.com.unb.smms.domain.Friends
import br.com.unb.smms.domain.NodeGraph
import br.com.unb.smms.repository.dto.*
import br.com.unb.smms.repository.mapper.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mapstruct.factory.Mappers
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


interface FacebookPageService {

    @POST("{page-id}/feed")
    fun feed(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @POST("{page-id}/photos")
    fun photo(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

}

class SmmsPageRepository(val context: Context, val baseUrl: String, val accessToken: String?) : SmmsRetrofit(context, baseUrl, accessToken) {

    private val facebookService = retrofit.create(FacebookPageService::class.java)

    fun feed(id: String, feed: Feed): Single<NodeGraph?> {

        val domainMapper = Mappers.getMapper(FeedResponseMapper::class.java)
        val nodeGraphDomainMapper = Mappers.getMapper(NodeGraphMapper::class.java)
        val dto = domainMapper.toDTO(feed)

        return facebookService.feed(id, dto).map { nodeDTO ->
            nodeGraphDomainMapper.toDomain(nodeDTO)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun photo(id: String, feed: Feed): Single<NodeGraph?> {

        val domainMapper = Mappers.getMapper(FeedResponseMapper::class.java)
        val nodeGraphDomainMapper = Mappers.getMapper(NodeGraphMapper::class.java)
        val dto = domainMapper.toDTO(feed)

        return facebookService.photo(id, dto).map { nodeDTO ->
            nodeGraphDomainMapper.toDomain(nodeDTO)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }
}
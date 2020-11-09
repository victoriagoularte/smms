package br.com.unb.smms.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.unb.smms.domain.facebook.*
import br.com.unb.smms.repository.dto.*
import br.com.unb.smms.repository.mapper.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.mapstruct.factory.Mappers
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject


interface FacebookPageService {

    @POST("{page-id}/feed")
    fun feed(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @POST("{page-id}/photos")
    fun photo(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-id}/picture?height=200&width=200&type=normal")
    fun photoProfile(@Path("page-id") id: String): Single<ResponseBody>

    @GET("{page-id}/posts")
    fun posts(@Path("page-id") id: String): Single<ListPostDTO>

    @GET("{page-id}?fields=instagram_business_account")
    fun igBusinessAccount(@Path("page-id") id: String): Single<IgBusinessAccountDTO>

    @POST("{ig-user-id}/media")
    fun mediaIg(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-post-id}/{metric}?summary=total_count")
    fun postInsights(@Path("page-post-id") id: String, @Path("metric") metric: String): Single<DataDTO>

//    @GET("{page-post-id}/comments?summary=total_count")
//    fun postComments(@Path("page-post-id") id: String): Single<DataDTO>
//
//    @GET("{page-post-id}/reactions")
//    fun postReactions(@Path("page-post-id") id: String): Single<DataDTO>

}

class PageRepository @Inject constructor(private val facebookService: FacebookPageService) {

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

    fun profilePicture(id: String): Single<Bitmap> {
        return facebookService.photoProfile(id).map { it ->
            BitmapFactory.decodeByteArray(it.bytes(), 0, it.contentLength().toInt())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun posts(id: String): Single<ListPost?> {

        val mapper = Mappers.getMapper(PostMapper::class.java)

        return facebookService.posts(id).map { dto ->
            mapper.toDomain(dto)
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

    fun igBusinessAccount(id: String): Single<IgBusinessAccount?> {

        val domainMapper = Mappers.getMapper(IgBusinessAccountMapper::class.java)

        return facebookService.igBusinessAccount(id).map { dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun postInsights(id: String, insight: String): Single<Int?> {

        val domainMapper = Mappers.getMapper(DataResponseMapper::class.java)

        return facebookService.postInsights(id, insight).map { dto ->
            dto.summary?.totalCount
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun postInsights(idsPosts: List<String>, metric: String): List<Single<Int?>> {
        return idsPosts.map { postInsights(it, metric) }
    }


}
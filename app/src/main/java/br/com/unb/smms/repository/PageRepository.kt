package br.com.unb.smms.repository

import br.com.unb.smms.domain.facebook.*
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.repository.dto.*
import br.com.unb.smms.repository.mapper.*
import io.reactivex.Flowable.fromIterable
import io.reactivex.Observable
import io.reactivex.Observable.fromIterable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mapstruct.factory.Mappers
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*
import javax.inject.Inject


interface FacebookPageService {

    @POST("{page-id}/feed")
    fun feed(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @POST("{page-id}/photos")
    fun photo(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-id}/posts")
    fun posts(@Path("page-id") id: String): Single<ListPostDTO>

    @GET("{page-id}?fields=instagram_business_account")
    fun igBusinessAccount(@Path("page-id") id: String): Single<IgBusinessAccountDTO>

    @POST("{ig-user-id}/media")
    fun mediaIg(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-post-id}/likes?summary=total_count")
    fun postLikes(@Path("page-post-id") id: String): Single<DataDTO>

    @GET("{page-post-id}/comments?summary=total_count")
    fun postComments(@Path("page-post-id") id: String): Single<DataDTO>

    @GET("{page-post-id}/reactions")
    fun postReactions(@Path("page-post-id") id: String): Single<DataDTO>

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

    fun postLikes(id: String): Single<Int?> {

        val domainMapper = Mappers.getMapper(DataResponseMapper::class.java)

        return facebookService.postLikes(id).map { dto ->
            dto.summary?.totalCount
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun postLikes(idsPosts: List<String>): List<Single<Int?>> {
        return idsPosts.map { postLikes(it) }
    }

}
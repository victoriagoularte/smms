package br.com.unb.smms.repository

import android.content.Context
import br.com.unb.smms.domain.facebook.*
import br.com.unb.smms.repository.dto.*
import br.com.unb.smms.repository.mapper.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mapstruct.factory.Mappers
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface FacebookPageService {

    @POST("{page-id}/feed")
    fun feed(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @POST("{page-id}/photos")
    fun photo(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-id}?fields=instagram_business_account")
    fun igBusinessAccount(@Path("page-id") id: String): Single<IgBusinessAccountDTO>

    @POST("{ig-user-id}/media")
    fun mediaIg(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-id}/insights?fields=period={period}&metric= page_engaged_users,page_actions_post_reactions_like_total, page_impressions, page_post_engagements")
    fun insights(
        @Path("page-id") id: String,
        @Path("period") metric: String
    ): Single<ListInsightDTO>

    @GET("{post-id}/comments?summary=total_count")
    fun postComments(@Path("post-id") id: String) : Single<FriendsDTO>

    @GET("{post-id}/likes?summary=total_count")
    fun postLikes(@Path("post-id") id: String) : Single<FriendsDTO>

}

class PageRepository(val context: Context, val baseUrl: String, val accessToken: String) : SmmsRetrofit(context, baseUrl, accessToken) {

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

    fun igBusinessAccount(id: String): Single<IgBusinessAccount?> {

        val domainMapper = Mappers.getMapper(IgBusinessAccountMapper::class.java)

        return facebookService.igBusinessAccount(id).map { dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun insights(id: String, period: String): Single<ListInsight?> {

        val domainMapper = Mappers.getMapper(InsightMapper::class.java)

        return facebookService.insights(id, period).map { dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun comments(id: String) : Single<Friends> {
        val domainMapper = Mappers.getMapper(FriendsResponseMapper::class.java)

        return facebookService.postComments(id).map {dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun likes(id: String) : Single<Friends> {
        val domainMapper = Mappers.getMapper(FriendsResponseMapper::class.java)

        return facebookService.postLikes(id).map {dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
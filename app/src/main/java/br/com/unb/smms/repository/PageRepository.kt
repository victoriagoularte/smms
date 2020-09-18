package br.com.unb.smms.repository

import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.domain.facebook.IgBusinessAccount
import br.com.unb.smms.domain.facebook.NodeGraph
import br.com.unb.smms.repository.dto.FeedDTO
import br.com.unb.smms.repository.dto.IgBusinessAccountDTO
import br.com.unb.smms.repository.dto.NodeGraphDTO
import br.com.unb.smms.repository.mapper.FeedResponseMapper
import br.com.unb.smms.repository.mapper.IgBusinessAccountMapper
import br.com.unb.smms.repository.mapper.NodeGraphMapper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    @GET("{page-id}?fields=instagram_business_account")
    fun igBusinessAccount(@Path("page-id") id: String): Single<IgBusinessAccountDTO>

    @POST("{ig-user-id}/media")
    fun mediaIg(@Path("page-id") id: String, @Body feedDTO: FeedDTO): Single<NodeGraphDTO>

    @GET("{page-post-id}/likes")
    fun postLikes(@Path("page-post-id") id: String): Single<NodeGraphDTO>

    @GET("{page-post-id}/reactions")
    fun postReactions(@Path("page-post-id") id: String): Single<NodeGraphDTO>

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
}
package br.com.unb.smms.repository

import android.content.Context
import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.domain.facebook.ListInsight
import br.com.unb.smms.repository.dto.IgInfoDTO
import br.com.unb.smms.repository.dto.ListInsightDTO
import br.com.unb.smms.repository.mapper.IgInfoMapper
import br.com.unb.smms.repository.mapper.InsightMapper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mapstruct.factory.Mappers
import retrofit2.http.GET
import retrofit2.http.Path


interface IgService {

    @GET("{ig-user-id}?fields=follows_count,followers_count,media_count")
    fun igInfo(@Path("ig-user-id") userId: String): Single<IgInfoDTO>

    @GET("{ig-user-id}/insights?metric=impressions,reach,profile_views&period={period}")
    fun insights(
        @Path("ig-user-id") id: String,
        @Path("period") metric: String
    ): Single<ListInsightDTO>

}

class IgRepository(val context: Context, val baseUrl: String, val accessToken: String?) :
    SmmsRetrofit(context, baseUrl, accessToken) {

    private val igService = retrofit.create(IgService::class.java)


    fun igInfo(id: String): Single<IgInfo> {

        val domainMapper = Mappers.getMapper(IgInfoMapper::class.java)

        return igService.igInfo(id).map { dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun insightsIg(id: String, period: String): Single<ListInsight?> {

        val domainMapper = Mappers.getMapper(InsightMapper::class.java)

        return igService.insights(id, period).map { dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}
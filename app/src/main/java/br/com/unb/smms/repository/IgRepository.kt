package br.com.unb.smms.repository

import br.com.unb.smms.domain.facebook.IgInfo
import br.com.unb.smms.repository.dto.IgInfoDTO
import br.com.unb.smms.repository.mapper.IgInfoMapper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.mapstruct.factory.Mappers
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject


interface IgService {

    @GET("{ig-user-id}?fields=follows_count,followers_count,media_count")
    fun igInfo(@Path("ig-user-id") userId: String): Single<IgInfoDTO>

}

class IgRepository @Inject constructor(private val igService: IgService) {

    fun igInfo(id: String): Single<IgInfo> {

        val domainMapper = Mappers.getMapper(IgInfoMapper::class.java)

        return igService.igInfo(id).map { dto ->
            domainMapper.toDomain(dto)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}
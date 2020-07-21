package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.repository.dto.FeedDTO
import org.mapstruct.Mapper

@Mapper
interface FeedResponseMapper {

    fun toDomain(dto: FeedDTO): Feed

    fun toDTO(feed: Feed): FeedDTO
}

package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.FanCount
import br.com.unb.smms.domain.facebook.Feed
import br.com.unb.smms.repository.dto.FanCountDTO
import br.com.unb.smms.repository.dto.FeedDTO
import org.mapstruct.Mapper

@Mapper
interface FanCountMapper {
    fun toDomain(dto: FanCountDTO): FanCount

    fun toDTO(feed: FanCount): FanCountDTO
}
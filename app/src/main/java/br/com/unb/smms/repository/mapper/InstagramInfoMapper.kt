package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.InstagramInfo
import br.com.unb.smms.repository.dto.InstagramInfoDTO
import org.mapstruct.Mapper

@Mapper
interface InstagramInfoMapper {

    fun toDomain(dto: InstagramInfoDTO): InstagramInfo
}
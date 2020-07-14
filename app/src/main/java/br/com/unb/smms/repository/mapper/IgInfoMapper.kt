package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.IgInfo
import br.com.unb.smms.repository.dto.IgInfoDTO
import org.mapstruct.Mapper

@Mapper
interface IgInfoMapper {

    fun toDomain(dto: IgInfoDTO): IgInfo
}
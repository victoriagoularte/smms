package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.ListPost
import br.com.unb.smms.repository.dto.ListPostDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper
interface PostMapper {

    fun toDomain(dto: ListPostDTO): ListPost

    fun toDTO(list: ListPost): ListPostDTO

}
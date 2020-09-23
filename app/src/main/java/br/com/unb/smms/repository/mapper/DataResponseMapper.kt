package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.Data
import br.com.unb.smms.repository.dto.DataDTO
import org.mapstruct.Mapper


@Mapper
interface DataResponseMapper {

    fun toDomain(dto: DataDTO): Data
}

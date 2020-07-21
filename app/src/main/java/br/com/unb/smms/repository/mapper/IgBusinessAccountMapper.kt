package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.IgBusinessAccount
import br.com.unb.smms.repository.dto.IgBusinessAccountDTO
import org.mapstruct.Mapper

@Mapper
interface IgBusinessAccountMapper {

    fun toDomain(dto: IgBusinessAccountDTO): IgBusinessAccount

}
package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.Friends
import br.com.unb.smms.repository.dto.FriendsDTO
import org.mapstruct.Mapper


@Mapper
interface FriendsResponseMapper {

    fun toDomain(dto: FriendsDTO): Friends
}

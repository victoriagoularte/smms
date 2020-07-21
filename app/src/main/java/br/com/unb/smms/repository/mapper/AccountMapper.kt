package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.Account
import br.com.unb.smms.repository.dto.AccountDTO
import org.mapstruct.Mapper

@Mapper
interface AccountMapper {

    fun toDomain(dto: AccountDTO): Account

}
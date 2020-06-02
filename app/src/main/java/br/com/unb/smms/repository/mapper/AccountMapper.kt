package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.Account
import br.com.unb.smms.domain.AccountList
import br.com.unb.smms.repository.dto.AccountDTO
import br.com.unb.smms.repository.dto.AccessDTO
import org.mapstruct.Mapper

@Mapper
interface AccountMapper {

    fun toDomain(dto: AccountDTO): Account

}
package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.facebook.IgBusinessAccount
import br.com.unb.smms.domain.facebook.ListInsight
import br.com.unb.smms.repository.dto.IgBusinessAccountDTO
import br.com.unb.smms.repository.dto.InsightDTO
import br.com.unb.smms.repository.dto.ListInsightDTO
import org.mapstruct.Mapper

@Mapper
interface InsightMapper {

    fun toDomain(dto: ListInsightDTO): ListInsight
}
package br.com.unb.smms.repository.mapper

import br.com.unb.smms.domain.NodeGraph
import br.com.unb.smms.repository.dto.NodeGraphDTO
import org.mapstruct.Mapper

@Mapper
interface NodeGraphMapper {

    fun toDomain(dto: NodeGraphDTO): NodeGraph

    fun toDTO(domain: NodeGraph): NodeGraphDTO
}

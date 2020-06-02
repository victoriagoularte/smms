package br.com.unb.smms.repository.dto

import com.google.gson.annotations.SerializedName

data class SummaryDTO (
    @SerializedName("total_count")
    var totalCount: Int? = null
)

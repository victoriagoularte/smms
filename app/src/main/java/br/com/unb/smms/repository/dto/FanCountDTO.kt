package br.com.unb.smms.repository.dto

import com.google.gson.annotations.SerializedName

data class FanCountDTO (
    @SerializedName("fan_count")
    var fanCount: Int = 0,
)
package br.com.unb.smms.repository.dto

import com.google.gson.annotations.SerializedName

data class IgInfoDTO(

    @SerializedName("follows_count")
    var followsCount: Int = 0,

    @SerializedName("followers_count")
    var followersCount: Int = 0,

    @SerializedName("media_count")
    var mediaCount: Int = 0

)
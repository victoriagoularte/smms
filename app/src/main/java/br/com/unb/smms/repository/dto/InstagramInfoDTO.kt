package br.com.unb.smms.repository.dto

data class InstagramInfoDTO(
    var follow_count: Int = 0,
    var followed_by_count: Int = 0,
    var profile_pic: String? = null,
    var id: Int = 0
)
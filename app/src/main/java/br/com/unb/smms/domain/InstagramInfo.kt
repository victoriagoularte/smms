package br.com.unb.smms.domain

data class InstagramInfo(
    var follow_count: Int = 0,
    var followed_by_count: Int = 0,
    var profile_pic: String? = null,
    var id: Int = 0
)
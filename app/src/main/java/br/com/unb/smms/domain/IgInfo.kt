package br.com.unb.smms.domain

data class IgInfo(
    var followsCount: Int = 0,
    var followersCount: Int = 0,
    var mediaCount: Int? = null
)
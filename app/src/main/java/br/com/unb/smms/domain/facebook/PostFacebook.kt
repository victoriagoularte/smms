package br.com.unb.smms.domain.facebook

import java.time.LocalDate

data class PostFacebook (
    var created_time: String? = null,
    var message: String? = null,
    var story: String? = null,
    var id: String? = null,
    var date: LocalDate? = null
)
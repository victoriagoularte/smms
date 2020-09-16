package br.com.unb.smms.domain.firebase


data class Post (
    var uid: String? = null,
    var title : String? = null,
    var body: String? = null,
    var postId : String? = null,
    var urlPicture: String? = null,
    var date: String? = null,
    var annotations: List<Tag>? = null,
    var category: String? = null
)
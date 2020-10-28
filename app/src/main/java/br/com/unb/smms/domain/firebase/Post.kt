package br.com.unb.smms.domain.firebase


data class Post (
    var id: String? = null,
    var uid: String? = null,
    var title : String? = null,
    var body: String? = null,
    var postId : String? = null,
    var urlPicture: String? = null,
    var date: String? = null,
    var month: String? = null,
    var year: String? = null,
    var annotations: List<Tag>? = null,
    var category: String? = null,
    var media: List<String>? = null,
    var pending: Boolean = false
)
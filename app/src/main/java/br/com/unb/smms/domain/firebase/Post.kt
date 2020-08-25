package br.com.unb.smms.domain.firebase


data class Post (
    var uid: String? = null,
    var title : String? = null,
    var body: String? = null,
    var postId : String? = null,
    var urlPicture: String? = null,
    var annotations: List<Tag>? = null,
    var categories: Array<Category>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (uid != other.uid) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (postId != other.postId) return false
        if (urlPicture != other.urlPicture) return false
        if (annotations != other.annotations) return false
        if (categories != null) {
            if (other.categories == null) return false
            if (!categories!!.contentEquals(other.categories!!)) return false
        } else if (other.categories != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + (postId?.hashCode() ?: 0)
        result = 31 * result + (urlPicture?.hashCode() ?: 0)
        result = 31 * result + (annotations?.hashCode() ?: 0)
        result = 31 * result + (categories?.contentHashCode() ?: 0)
        return result
    }
}
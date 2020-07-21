package br.com.unb.smms.domain.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post (
    var uid: String? = "",
    var title : String? = "",
    var body: String? = "",
    var urlPicture: String? = "",
    var annotations: Array<Annotation>? = null,
    var categories: Array<Category>? = null
) {

    @Exclude
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "title" to title,
            "body" to body,
            "urlPicture" to urlPicture,
            "annotations" to annotations,
            "categories" to categories
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (uid != other.uid) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (urlPicture != other.urlPicture) return false
        if (annotations != null) {
            if (other.annotations == null) return false
            if (!annotations!!.contentEquals(other.annotations!!)) return false
        } else if (other.annotations != null) return false
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
        result = 31 * result + (urlPicture?.hashCode() ?: 0)
        result = 31 * result + (annotations?.contentHashCode() ?: 0)
        result = 31 * result + (categories?.contentHashCode() ?: 0)
        return result
    }
}


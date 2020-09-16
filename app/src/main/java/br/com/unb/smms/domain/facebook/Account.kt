package br.com.unb.smms.domain.facebook

data class Account(
    var categoryList: Array<NodeGraph>? = null,
    var accessToken: String? = null,
    var id: String? = "109187477422515",
    var name: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (categoryList != null) {
            if (other.categoryList == null) return false
            if (!categoryList!!.contentEquals(other.categoryList!!)) return false
        } else if (other.categoryList != null) return false
        if (accessToken != other.accessToken) return false
        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryList?.contentHashCode() ?: 0
        result = 31 * result + (accessToken?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}
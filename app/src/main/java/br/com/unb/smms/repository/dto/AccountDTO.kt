package br.com.unb.smms.repository.dto

import com.google.gson.annotations.SerializedName

data class AccountDTO(
    @SerializedName("category_list")
    var categoryList: Array<NodeGraphDTO>? = null,

    @SerializedName("access_token")
    var accessToken: String? = null,

    var id: String? = null,
    var name: String? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountDTO

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

package br.com.unb.smms.repository.dto

data class FriendsDTO(
    var data: Array<Any>? = null,
    var summary: SummaryDTO? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FriendsDTO

        if (data != null) {
            if (other.data == null) return false
            if (!data!!.contentEquals(other.data!!)) return false
        } else if (other.data != null) return false
        if (summary != other.summary) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.contentHashCode() ?: 0
        result = 31 * result + (summary?.hashCode() ?: 0)
        return result
    }
}

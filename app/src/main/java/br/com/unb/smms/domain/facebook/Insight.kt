package br.com.unb.smms.domain.facebook

import java.util.*

data class ListInsight(
    var data: Array<Insight>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListInsight

        if (data != null) {
            if (other.data == null) return false
            if (!data!!.contentEquals(other.data!!)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        return data?.contentHashCode() ?: 0
    }
}

data class Insight(
    var name: String? = null,
    var period: String? = null,
    var values: Array<ValueInsight>? = null,
    var title: String? = null,
    var description: String? = null,
    var id: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Insight

        if (name != other.name) return false
        if (period != other.period) return false
        if (values != null) {
            if (other.values == null) return false
            if (!values!!.contentEquals(other.values!!)) return false
        } else if (other.values != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (period?.hashCode() ?: 0)
        result = 31 * result + (values?.contentHashCode() ?: 0)
        return result
    }
}

data class ValueInsight(
    var value: Int? = null,
    var end_time: Date? = null
)

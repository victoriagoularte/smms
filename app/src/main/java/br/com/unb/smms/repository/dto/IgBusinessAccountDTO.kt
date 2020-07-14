package br.com.unb.smms.repository.dto

import com.google.gson.annotations.SerializedName

data class IgBusinessAccountDTO(

    @SerializedName("instagram_business_account")
    var igBusinessAccount: NodeGraphDTO? = null

)
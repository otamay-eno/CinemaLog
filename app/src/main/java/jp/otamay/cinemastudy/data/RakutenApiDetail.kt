package jp.otamay.cinemastudy.data

import com.google.gson.annotations.SerializedName

data class RakutenApiDetail(
    @SerializedName("Item") val item: RakutenApiDetails
)

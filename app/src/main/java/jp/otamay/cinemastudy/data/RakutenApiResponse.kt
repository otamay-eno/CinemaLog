package jp.otamay.cinemastudy.data

import com.google.gson.annotations.SerializedName

data class RakutenApiResponse(
    @SerializedName("GenreInformation") val genreInformation: List<Any>,
    @SerializedName("Items") val items: List<RakutenApiDetail>,
    @SerializedName("carrier") val carrier: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("first") val first: Int,
    @SerializedName("hits") val hits: Int,
    @SerializedName("last") val last: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("pageCount") val pageCount: Int
)

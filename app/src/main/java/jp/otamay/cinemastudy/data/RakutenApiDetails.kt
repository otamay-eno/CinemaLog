package jp.otamay.cinemastudy.data

import com.google.gson.annotations.SerializedName

data class RakutenApiDetails(
    @SerializedName("affiliateUrl") val affiliateUrl: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("artistNameKana") val artistNameKana: String,
    @SerializedName("availability") val availability: String,
    @SerializedName("booksGenreId") val booksGenreId: String,
    @SerializedName("discountPrice") val discountPrice: Int,
    @SerializedName("discountRate") val discountRate: Int,
    @SerializedName("itemCaption") val itemCaption: String,
    @SerializedName("itemPrice") val itemPrice: Int,
    @SerializedName("itemUrl") val itemUrl: String,
    @SerializedName("jan") val jan: String,
    @SerializedName("label") val label: String,
    @SerializedName("largeImageUrl") val largeImageUrl: String,
    @SerializedName("limitedFlag") val limitedFlag: Int,
    @SerializedName("listPrice") val listPrice: Int,
    @SerializedName("makerCode") val makerCode: String,
    @SerializedName("mediumImageUrl") val mediumImageUrl: String,
    @SerializedName("postageFlag") val postageFlag: Int,
    @SerializedName("reviewAverage") val reviewAverage: String,
    @SerializedName("reviewCount") val reviewCount: Int,
    @SerializedName("salesDate") val salesDate: String,
    @SerializedName("smallImageUrl") val smallImageUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("titleKana") val titleKana: String
)
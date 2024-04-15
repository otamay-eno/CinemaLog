package jp.otamay.cinemastudy.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 【機能】画面遷移用データクラス
 *
 * 【概要】画面遷移時に保持するデータをまとめたクラスです
 *
 * 【作成日、作成者】2024/04/12 N.OONISHI
 */
data class MovieContainer(
    @SerializedName("tmp") val tmp: String,
    @SerializedName("date") val date: String?,
    @SerializedName("director") var director: String,
    @SerializedName("title") val title: String?,
    @SerializedName("image") val image: ByteArray,
    @SerializedName("originalTitle") var originalTitle: String,
    @SerializedName("poster_path") var posterPath: String,
    @SerializedName("year") val year: String,
    @SerializedName("movie_count") val movieCount: Int
): Serializable

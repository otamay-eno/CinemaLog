package jp.otamay.cinemastudy.data

import androidx.room.ColumnInfo
import java.io.Serializable

data class MovieStatistics(
    @ColumnInfo(name = "year")
    val year: String,
    @ColumnInfo(name = "movie_count")
    val movieCount: Int
): Serializable

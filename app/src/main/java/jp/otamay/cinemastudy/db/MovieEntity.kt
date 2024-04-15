package jp.otamay.cinemastudy.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_movie")
data class MovieEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tmp")
    val tmp: String,
    @ColumnInfo(name = "date")
    val date: String?,
    @ColumnInfo(name = "director")
    val director: String?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "image")
    val image: ByteArray
): Serializable


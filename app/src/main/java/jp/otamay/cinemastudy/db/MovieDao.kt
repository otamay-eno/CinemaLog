package jp.otamay.cinemastudy.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import jp.otamay.cinemastudy.data.MovieStatistics

@Dao
interface MovieDao {
    @RawQuery
    fun executeQuery(query: SupportSQLiteQuery): List<MovieEntity>
    @Query("SELECT SUBSTR(date, 1, 4) AS year, COUNT(*) AS movie_count FROM table_movie GROUP BY SUBSTR(date, 1, 4) ORDER BY year desc")
    fun getMovieByYear(): List<MovieStatistics>
}
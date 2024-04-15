package jp.otamay.cinemastudy.api

import jp.otamay.cinemastudy.data.RakutenApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 04/04
interface RakutenService {
    @GET("BooksDVD/Search/20170404")
    fun searchMovies(
        @Query("applicationId") applicationId: String,
        @Query("title") title: String
    ): Call<RakutenApiResponse>
}
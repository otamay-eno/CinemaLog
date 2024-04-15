package jp.otamay.cinemastudy.base

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import jp.otamay.cinemastudy.data.MovieContainer
import jp.otamay.cinemastudy.db.MovieDataBase
import jp.otamay.cinemastudy.db.MovieEntity

class BaseBusinessLogic {

    /**
     * 【クラス】データベース処理結果データクラス
     *
     * 【概要】データベース処理結果と成功(true)失敗(false)を設定します
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     */
    data class DataBaseResult (val movieEntity: List<MovieContainer>, val isCheck: Boolean)

    /**
     * 【機能】
     * 任意のSQLを実行
     *
     * 【概要】
     * コンテキストとSQL文、パラメーターを設定することで任意のSQLを非同期で実行し、成功・失敗を判定
     *
     * 【作成日・作成者】
     * 2024/03/22 N.OONISHI
     *
     * 【更新日・更新者】
     * 2024/03/23 N.OONISHI 成功・失敗の結果が分かるように変更
     *
     * @param context 実行するActivityのコンテキスト
     * @param sql 実行したいSQL
     * @param parameters SQLに設定するパラメーター
     * @return 実行結果（成功・失敗）
     */
    fun executeAsyncCallBack(context: Context, sql: String, vararg parameters: Any): DataBaseResult {
        val supportQuery = SimpleSQLiteQuery(sql, parameters)
        return executeAsyncCallBack(context, supportQuery)
    }

    /**
     * 【機能】
     * 任意のSQLを実行
     *
     * 【概要】
     * コンテキストとSQL文、パラメーターを設定することで任意のSQLを非同期で実行し、成功・失敗を判定
     *
     * 【作成日・作成者】
     * 2024/03/22 N.OONISHI
     *
     * 【更新日・更新者】
     * 2024/03/23 N.OONISHI 成功・失敗の結果が分かるように変更
     *
     * @param context 実行するActivityのコンテキスト
     * @param supportQuery 実行したいSQL、SQLに設定するパラメーター
     * @return 実行結果（成功・失敗）
     */
    private fun executeAsyncCallBack(context: Context, supportQuery: SupportSQLiteQuery): DataBaseResult {
        // Roomデータベースのインスタンスを取得
        val db = MovieDataBase.getInstance(context)
        // 任意のSQLを実行
        return try {
            // 成功
            DataBaseResult(dataConversion(db.movieDao().executeQuery(supportQuery)), true)
        } catch (e: Exception) {
            //　失敗
            DataBaseResult(emptyList(), false)
        }
    }

    private fun dataConversion(data: List<MovieEntity>): List<MovieContainer> {
        return data.map { movieEntity ->
            MovieContainer(movieEntity.tmp, movieEntity.date, movieEntity.director.toString()
                , movieEntity.title, movieEntity.image, "", "", "", 0)
        }
    }
}
package jp.otamay.cinemastudy.base

class Const {
    companion object {
        /**
         * M001MainActivityで使用
         * 画面に表示する映画情報を新しい順で全件取得
         */
        const val SQL_SELECT_ALL_NEW = "SELECT tmp, date, director, title, image FROM table_movie ORDER BY date desc"
        /**
         * M001MainActivityで使用
         * 画面に表示する映画情報を古い順で全件取得
         */
        const val SQL_SELECT_ALL_OLD = "SELECT tmp, date, director, title, image FROM table_movie ORDER BY date asc"
        /**
         * M005RegisterActivity、M004ManualInputFragmentで使用
         * 映画情報をデータベースに登録
         */
        const val SQL_INSERT_MOVIE_INFO = "INSERT INTO table_movie (tmp, date, director, title, image) VALUES (?, ?, ?, ?, ?)"
        /**
         * M006EditActivityで使用
         * 映画情報を更新
         */
        const val SQL_UPDATE_MOVIE_INFO = "UPDATE table_movie SET date = ?, director = ?, title = ?, image = ? WHERE tmp = ?"
        /**
         * M006EditActivityで使用
         * 映画情報を削除
         */
        const val SQL_DELETE_MOVIE_INFO = "DELETE FROM table_movie WHERE tmp = ?"
        /**
         * M007FindInTerminalで使用
         * タイトルから映画情報検索SQL
         */
        const val SQL_SELECT_BY_TITLE = "SELECT tmp, date, director, title, image FROM table_movie WHERE title LIKE '%' || ? || '%'"
        /**
         * M007FindInTerminalで使用
         * 監督名から映画情報検索SQL
         */
        const val SQL_SELECT_BY_DIRECTOR = "SELECT tmp, date, director, title, image FROM table_movie WHERE director LIKE '%' || ? || '%'"
        /**
         * M008DisplayPerYearsActivityで使用
         * 画面に表示する映画情報を選択された年ごとに新しい順で全件取得
         */
        const val SQL_SELECT_NEW_PER_YEAR = "SELECT tmp, date, director, title, image FROM table_movie WHERE date LIKE ? || '%' ORDER BY date desc"
        /**
         * M008DisplayPerYearsActivityで使用
         * 画面に表示する映画情報を選択された年ごとに古い順で全件取得
         */
        const val SQL_SELECT_OLD_PER_YEAR = "SELECT tmp, date, director, title, image FROM table_movie WHERE date LIKE ? || '%' ORDER BY date asc"
    }
}
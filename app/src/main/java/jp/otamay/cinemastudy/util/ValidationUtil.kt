package jp.otamay.cinemastudy.util

import android.widget.EditText

/**
 * 【機能】入力チェックUtil<br>
 * 【概要】入力チェックを汎用化したクラスです<br>
 * 【作成日・作成者】2024/03/22 N.OONISHI<br>
 */
class ValidationUtil {

    companion object {
        /**
         * 【機能】入力チェック
         * 【概要】引数にEditTextのインスタンスを受け取り、null・空文字のチェックを行います
         * 【作成日・作成者】2024/03/22 N.OONISHI
         *
         * @param ed1　入力チェックを行うEditTextのインスタンス
         * @param ed2　入力チェックを行うEditTextのインスタンス
         * @param ed3　入力チェックを行うEditTextのインスタンス
         * @return null or 空文字の場合 false
         */
        fun isInputCheck(ed1: EditText?, ed2: EditText?, ed3: EditText?): Boolean {
            // Null チェック
            if (ed1 == null || ed2 == null || ed3 == null) {
                return false
            }
            // EditTextからテキストを取得
            val text1 = ed1.text.toString().trim()
            val text2 = ed2.text.toString().trim()
            val text3 = ed3.text.toString().trim()

            // テキストが空白かどうかを判定
            return text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty()
        }

        /**
         * 【機能】入力チェック
         * 【概要】引数にEditTextのインスタンスを受け取り、null・空文字のチェックを行います
         * 【作成日・作成者】2024/01/29
         *
         * @param ed1　入力チェックを行うEditTextのインスタンス
         * @param ed2　入力チェックを行うEditTextのインスタンス
         * @return null or 空文字の場合 false
         */
        fun isInputCheck(ed1: EditText?, ed2: EditText?): Boolean {
            // Null チェック
            if (ed1 == null || ed2 == null) {
                return false
            }
            // EditTextからテキストを取得
            val text1 = ed1.text.toString().trim()
            val text2 = ed2.text.toString().trim()

            // テキストが空白かどうかを判定
            return text1.isNotEmpty() && text2.isNotEmpty()
        }

        /**
         * 【機能】入力チェック
         * 【概要】引数にEditTextのインスタンスを受け取り、null・空文字のチェックを行います
         * 【作成日・作成者】2024/01/29
         *
         * @param ed1　入力チェックを行うEditTextのインスタンス
         * @return null or 空文字の場合 false
         */
        fun isInputCheck(ed1: EditText?): Boolean {
            // Null チェック
            if (ed1 == null) {
                return false
            }
            // EditTextからテキストを取得
            val text1 = ed1.text.toString().trim()

            // テキストが空白かどうかを判定
            return text1.isNotEmpty()
        }
    }

}
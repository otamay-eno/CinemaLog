package jp.otamay.cinemastudy.base

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import jp.otamay.cinemastudy.data.MovieContainer
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

open class BaseFragment: Fragment() {

    /**
     * 【機能】次画面遷移
     *
     * 【概要】引数に遷移先のクラスを指定し画面遷移を行います
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * @param cls 遷移先クラス
     */
    fun moveNextScreen(cls: Class<*>) {
        val intent = Intent(requireContext(), cls)
        startActivity(intent)
    }

    /**
     * 【機能】次画面遷移
     *
     * 【概要】引数に遷移先のクラスとIntentフラグを指定し画面遷移を行います
     *
     * 【作成日、作成者】2024/04/13 N.OONISHI
     *
     * @param cls 遷移先クラス
     * @param flags Intentフラグ
     */
    fun moveNextScreen(cls: Class<*> , flags: Int) {
        val intent = Intent(requireContext(), cls)
        intent.flags = flags
        startActivity(intent)
    }

    /**
     * 【機能】次画面遷移
     *
     * 【概要】引数に遷移先のクラスと遷移先のクラスに渡したいデータを指定し画面遷移を行います
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     *
     * @param cls 遷移先クラス
     * @param data 遷移先に渡したいデータ（MovieContainer型)
     */
    fun moveNextScreen(cls: Class<*>, data: MovieContainer) {
        val intent = Intent(requireContext(), cls)
        intent.putExtra(Key.KEY_INTENT, data)
        startActivity(intent)
    }

    /**
     * 【機能】<br>入力チェックダイアログ表示
     *
     * 【概要】<br>入力チェックエラー時に表示するダイアログです
     *
     * 【作成日・作成者】<br>2024/03/22 N.OONISHI
     */
    fun showInputDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setMessage("全項目入力してください！")
            setPositiveButton("OK") { dialog, which ->
                // ダイアログを閉じる
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        // ダイアログ外をタップしてもダイアログを閉じない
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    /**
     * 【機能】ダイアログ表示
     *
     * 【概要】引数のタイトル、メッセージをもとにダイアログを表示します
     *
     * 【作成日・作成者】<br>2024/03/23 N.OONISHI
     * 　
     * @param title ダイアログタイトル
     * @param message ダイアログメッセージ
     */
    fun showErrorDialog(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, which ->
                // ダイアログを閉じる
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        // ダイアログ外をタップしてもダイアログを閉じない
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


    /**
     * 【機能】ダイアログコールバックインターフェイス
     *
     * 【作成日、作成者】2024/03/22 N.OONISHI
     */
    interface DialogResultCallback {
        fun onResult(result: Boolean)
    }
    /**
     * 【機能】<br>ダイアログ表示
     *
     * 【概要】<br>任意のダイアログ設定をカスタムレイアウトに行いダイアログを表示します
     *
     * 【作成日・作成者】<br>2024/03/22 N.OONISHI
     * 　
     * @param dialogModel ダイアログモデル
     * @param callback 結果
     */
    fun showDialog(callback: DialogResultCallback) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setMessage("削除しますか？")
            setPositiveButton("はい") { dialog, which ->
                callback.onResult(true)
            }
            setNegativeButton("いいえ") { dialog, which ->
                callback.onResult(false)
            }
        }
        val alertDialog = alertDialogBuilder.create()
        // ダイアログ外をタップしてもダイアログを閉じない
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    /**
     * 【機能】タイムスタンプ取得
     *
     * 【概要】データベースの主キーであるタイムスタンプを取得します
     *
     * 【返却値】String型で次のような値を返却する　例）2000-09-28T01:02:26.585340800
     *
     * 【作成日、作成者】2024/03/26 N.OONISHI
     *
     * @return tmp yyyy-MM-dd'T'HH:mm:ss
     */
    fun getTmp(): String {
        // 現在の日時を取得し文字列へ変換
        return LocalDateTime.now().toString()
    }

    /**
     * 【機能】BitMapリサイズ
     *
     * 【概要】引数に受け取ったBitMapを指定のサイズでリサイズします
     *
     * 【作成日、作成者】2024/03/28 N.OONISHI
     *
     * @return BitMap リサイズしたBitMap
     */
    fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // アスペクト比を維持しながらリサイズする
        val scaleWidth = 520.toFloat() / width
        val scaleHeight = 740.toFloat() / height
        val scaleRatio = if (scaleWidth < scaleHeight) scaleWidth else scaleHeight

        val matrix = Matrix()
        matrix.postScale(scaleRatio, scaleRatio)

        // リサイズしたBitmapを作成
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
    }

    /**
     * 【機能】画像取得
     *
     * 【概要】引数に指定した画像リソースをByteArray型で返却
     *
     * 【作成日、作成者】2024/03/28 N.OONISHI
     *
     * @return ByteArray
     */
    fun getImageResourceAsByteArray(context: Context, resId: Int): ByteArray {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val outputStream = ByteArrayOutputStream()
        // PNG形式で圧縮
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * 【機能】ByteArray → BitMap
     *
     * 【概要】引数に指定したByteArray型画像リソースをBitMap型に変換
     *
     * 【作成日、作成者】2024/03/31 N.OONISHI
     *
     * @return Bitmap
     */
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    // 04/04
    fun returnDirectorNm(data: String): String {
        return if (data.isNotEmpty()) {
            data.substringAfterLast("/")
        } else {
            ""
        }
    }
}
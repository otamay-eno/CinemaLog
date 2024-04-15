package jp.otamay.cinemastudy.base

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.activity.M006EditActivity
import jp.otamay.cinemastudy.data.MovieContainer
import jp.otamay.cinemastudy.util.MainCardViewAdapter
import jp.otamay.cinemastudy.util.MovieApiCardViewAdapter
import jp.otamay.cinemastudy.util.YearCardViewAdapter
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class BaseActivity : AppCompatActivity() {

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
        val intent = Intent(this, cls)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
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
        val intent = Intent(this, cls)
        intent.putExtra(Key.KEY_INTENT, data)
        startActivity(intent)
    }

    /**
     * 【機能】アクションバー設定
     *
     * 【概要】アクションバーの設定・タイトルの表示を行います
     *
     * 【作成日・作成者】2024/01/23 N.OONISHI
     *
     * @param title アクションバーに設定したいタイトル
     */
    fun setActionBar(title: String) {
        // アクションバーを取得する
        val actionBar = supportActionBar
        // アクションバーの中央にテキストを表示させる
        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar)
        // アクションバーのタイトルを設定
        val titleView = actionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
        titleView?.text = title
    }

    /**
     * 【機能】<br>入力チェックダイアログ表示
     *
     * 【概要】<br>入力チェックエラー時に表示するダイアログです
     *
     * 【作成日・作成者】<br>2024/03/22 N.OONISHI
     */
    fun showInputDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setMessage("全項目入力してください！")
            setPositiveButton("OK") { dialog, _ ->
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
        val alertDialogBuilder = AlertDialog.Builder(this)
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
    fun showDeleteDialog(callback: DialogResultCallback) {
        val alertDialogBuilder = AlertDialog.Builder(this)
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

    /**
     * 【機能】画像ダウンロード
     *
     * 【概要】HTTPリクエストを送信して画像を取得する関数
     *
     * 【作成日、作成者】2024/04/02 N.OONISHI
     *
     * @return ByteArray
     */
    fun downloadImage(url: String): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connect()
            val inputStream = BufferedInputStream(connection.inputStream)
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            return outputStream.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    fun getToday(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        return currentDateTime.format(formatter)
    }

    // 04/04
    fun returnDirectorNm(data: String): String {
        return data.substringAfterLast("/")
    }

    /**
     * 【機能】画面項目設定
     *
     * 【概要】画面に表示する項目を設定します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * 【更新日、更新者】2024/04/09 N.OONISHI
     * 役割の分割のため処理を細分化
     *
     * @param recyclerView 表示先リサイクルビュー
     * @param data　表示させたいデータ
     */
    private fun setupRecyclerView(recyclerView: RecyclerView, data: List<MovieContainer>) {
        // RecyclerViewにAdapterをセット
        val adapter = createAdapter(data)
        recyclerView.adapter = adapter

        // レイアウトマネージャーを設定
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 【機能】画面項目設定
     *
     * 【概要】画面に表示する項目を設定します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * 【更新日、更新者】2024/04/09 N.OONISHI
     * 役割の分割のため処理を細分化
     *
     * @param data　表示させたいデータ
     */
    private fun createAdapter(data: List<MovieContainer>): MainCardViewAdapter {
        val adapter = MainCardViewAdapter(data)
        adapter.setOnItemClickListener { movieEntity ->
            moveNextScreen(M006EditActivity::class.java, movieEntity)
        }
        return adapter
    }

    /**
     * 【機能】画面項目設定
     *
     * 【概要】画面に表示する項目を設定します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * 【更新日、更新者】2024/04/09 N.OONISHI
     * 役割の分割のため処理を細分化
     *
     * @param recyclerView 表示先リサイクルビュー
     * @param data 表示させたいデータ
     * @param adapterCreator Adapterを生成する関数
     * @param onItemClick クリックリスナーのコールバック
     */
    fun <T : RecyclerView.ViewHolder> setupRecyclerView(
        recyclerView: RecyclerView,
        data: List<MovieContainer>,
        adapterCreator: (List<MovieContainer>) -> RecyclerView.Adapter<T>,
        onItemClick: (MovieContainer) -> Unit
    ) {
        // RecyclerViewにAdapterをセット
        val adapter = adapterCreator(data)
        recyclerView.adapter = adapter

        // レイアウトマネージャーを設定
        recyclerView.layoutManager = LinearLayoutManager(this)

        // onItemClickをリスナーとしてセットする処理はここでは行わない
    }

    /**
     * 【機能】Adapterを生成
     *
     * 【概要】Adapterを生成します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * 【更新日、更新者】2024/04/09 N.OONISHI
     * 役割の分割のため処理を細分化
     *
     * @param data 表示させたいデータ
     * @param onItemClick クリックリスナーのコールバック
     */
    fun createMainAdapter(
        data: List<MovieContainer>,
        onItemClick: (MovieContainer) -> Unit
    ): MainCardViewAdapter {
        val adapter = MainCardViewAdapter(data)
        adapter.setOnItemClickListener(onItemClick)
        return adapter
    }

    /**
     * 【機能】Adapterを生成
     *
     * 【概要】Adapterを生成します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * 【更新日、更新者】2024/04/09 N.OONISHI
     * 役割の分割のため処理を細分化
     *
     * @param data 表示させたいデータ
     * @param onItemClick クリックリスナーのコールバック
     */
    fun createYearAdapter(
        data: List<MovieContainer>,
        onItemClick: (MovieContainer) -> Unit
    ): YearCardViewAdapter {
        val adapter = YearCardViewAdapter(data)
        adapter.setOnItemClickListener(onItemClick)
        return adapter
    }

    /**
     * 【機能】Adapterを生成
     *
     * 【概要】Adapterを生成します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * 【更新日、更新者】2024/04/09 N.OONISHI
     * 役割の分割のため処理を細分化
     *
     * @param data 表示させたいデータ
     * @param onItemClick クリックリスナーのコールバック
     */
    fun createApiAdapter(
        data: List<MovieContainer>,
        onItemClick: (MovieContainer) -> Unit
    ): MovieApiCardViewAdapter {
        val adapter = MovieApiCardViewAdapter(data)
        adapter.setOnItemClickListener(onItemClick)
        return adapter
    }


}
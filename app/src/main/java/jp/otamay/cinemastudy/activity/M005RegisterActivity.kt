package jp.otamay.cinemastudy.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.base.BaseActivity
import jp.otamay.cinemastudy.base.BaseBusinessLogic
import jp.otamay.cinemastudy.base.Const
import jp.otamay.cinemastudy.base.Key
import jp.otamay.cinemastudy.data.MovieContainer
import jp.otamay.cinemastudy.fragment.M004ManualInputFragment
import jp.otamay.cinemastudy.util.DatePickerUtil
import jp.otamay.cinemastudy.util.ValidationUtil
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

/**
 * 【クラス】手入力画面
 *
 * 【機能】画面の入力により映画情報を更新、削除します
 */
class M005RegisterActivity : BaseActivity(), View.OnClickListener {

    /** タップされたデータ */
    private lateinit var data: MovieContainer

    /** 日付表示領域 */
    private lateinit var dateEdt: EditText

    /** 監督表示領域 */
    private lateinit var directorEdt: EditText

    /** 題名表示領域 */
    private lateinit var titleEdt: EditText

    /** ポスター画像表示領域 */
    private lateinit var imageButton: ImageButton

    /** 画像バイト */
    private var initByteArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m005_activity)
        // タップされたデータの取得
        data = intent.getSerializableExtra(Key.KEY_INTENT) as MovieContainer
        findViewById<Button>(R.id.input_button).setOnClickListener(this)
        findViewById<ImageButton>(R.id.date_picker_actions).setOnClickListener(this)
        findViewById<ImageButton>(R.id.imagebutton).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        // アクションバー設定
        setActionBar("登録")
        // UIの初期化
        dateEdt = findViewById(R.id.dateEdit)
        directorEdt = findViewById(R.id.directorEditText)
        titleEdt = findViewById(R.id.titleEditText)
        imageButton = findViewById(R.id.imagebutton)
        // キーパットからの入力制御
        dateEdt.isEnabled = false

        dateEdt.setText(getToday())
        directorEdt.setText(data.director)
        titleEdt.setText(data.originalTitle)
        // ポスター画像のパス取得
        val path = data.posterPath
        // 画像をロードして表示
        //あらかじめ表示オプションを指定しておく
        val option: RequestOptions = RequestOptions().fitCenter()
        //withでimageViewがあるActivityかFragmentを指定する
        //loadで画像のダウンロード先を指定する
        //intoで表示させるimageViewを指定する
        Glide.with(this).load(path).apply(option).into(imageButton)
    }

    /**
     * 【機能】端末内画像選択
     *
     * 【概要】「画像を選択」押下時に端末内の画像選択を表示
     *
     * 【作成日、作成者】2024/03/28 N.OONISHI
     */
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, M004ManualInputFragment.PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * 【機能】画像選択後戻り処理
     *
     * 【概要】端末内の選択した画像をByteArray型で保持します
     *
     * 【作成日、作成者】2024/03/28 N.OONISHI
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == M004ManualInputFragment.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            val bitmap = BitmapFactory.decodeStream(imageUri?.let {
                this.contentResolver.openInputStream(
                    it
                )
            })
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageButton.setImageBitmap(resizeBitmap(bitmap))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            initByteArray = byteArrayOutputStream.toByteArray()
        }
    }

    /**
     * 【機能】登録するボタン押下時処理
     *
     * 【概要】入力チェック後、DB登録処理を実行し成功時メイン画面へ遷移（失敗時ダイアログを表示）
     *
     *【作成日、作成者】2024/03/23 N.OONISHI
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun onClickInputButton() {
        // 入力チェックを行いNGの場合はダイアログを表示し、処理を中断
        if (!ValidationUtil.isInputCheck(dateEdt, directorEdt, titleEdt)) {
            showInputDialog()
            return
        }
        // 映画情報を非同期処理で格納
        GlobalScope.launch {
            if (registerData()) {
                // 格納成功時にメイン画面に遷移
                moveNextScreen(M001MainActivity::class.java)
                finish()
            } else {
                withContext(Dispatchers.Main) {
                    // 格納失敗時にダイアログを表示
                    showErrorDialog(
                        getString(R.string.error_dialog_title),
                        getString(R.string.error_dialog_message)
                    )
                }
            }
        }
    }

    /**
     * 【機能】DB登録処理
     *
     * 【概要】画面に入力された情報をもとにDBへ登録します
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     *
     * @return isCheck DB登録処理：成功(true) 失敗(false)
     */
    private suspend fun registerData(): Boolean {
        // APIで取得した画像の場合＝ダウンロードしByteArray型で保存：他の画像選択した場合その画像を保存
        val byteArray: ByteArray = if (initByteArray == null) {
            downloadImage(data.posterPath)
        } else {
            initByteArray as ByteArray
        }
        // ビジネスロジックを呼び出し画面に入力された映画情報を挿入
        val result = withContext(Dispatchers.IO) {
            BaseBusinessLogic().executeAsyncCallBack(
                applicationContext,
                Const.SQL_INSERT_MOVIE_INFO,
                getTmp(),
                dateEdt.text.toString(),
                directorEdt.text.toString(),
                titleEdt.text.toString(),
                byteArray
            )
        }
        return result.isCheck
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                // 「登録する」ボタン押下
                R.id.input_button ->
                    onClickInputButton()
                // 日付選択ボタン押下時
                R.id.date_picker_actions ->
                    // 日付選択ダイアログを表示
                    DatePickerUtil.showDatePicker(dateEdt)
                R.id.imagebutton ->
                    selectImage()
            }
        }
    }
}
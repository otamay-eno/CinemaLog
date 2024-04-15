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
 * 【クラス】編集画面
 *
 * 【機能】画面の入力により映画情報を更新、削除します
 */
class M006EditActivity : BaseActivity(), View.OnClickListener {

    /** タップされたデータ */
    private lateinit var data: MovieContainer

    /** 日付表示領域 */
    private lateinit var dateEdt: EditText

    /** 監督表示領域 */
    private lateinit var directorEdt: EditText

    /** 題名表示領域 */
    private lateinit var titleEdt: EditText

    /** 画像選択view */
    private lateinit var imageButton: ImageButton

    /** 画像バイト */
    private lateinit var byteArray: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m006_activity)
        // タップされたデータの取得
        data = intent.getSerializableExtra(Key.KEY_INTENT) as MovieContainer
        // 各ボタンのリスナー設定
        findViewById<Button>(R.id.update_button).setOnClickListener(this)
        findViewById<Button>(R.id.delete_button).setOnClickListener(this)
        findViewById<ImageButton>(R.id.date_picker_actions).setOnClickListener(this)
        imageButton = findViewById(R.id.imagebutton)
        imageButton.setOnClickListener(this)
        // ImageButtonの初期化
        // 画像未選択時の保存データ用に初期表示画像（NO IMAGE）をByteArray型で取得します
        byteArray = data.image
    }

    override fun onStart() {
        super.onStart()
        // アクションバー設定
        setActionBar("更新")
        // UIの初期化
        dateEdt = findViewById(R.id.dateEdit)
        directorEdt = findViewById(R.id.directorEditText)
        titleEdt = findViewById(R.id.titleEditText)
        // キーパットからの入力制御
        dateEdt.isEnabled = false
        // 初期値設定
        dateEdt.setText(data.date)
        directorEdt.setText(data.director)
        titleEdt.setText(data.title)
        imageButton.setImageBitmap(byteArrayToBitmap(byteArray)?.let { resizeBitmap(it) })
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
            byteArray = byteArrayOutputStream.toByteArray()
        }
    }

    /**
     * 【機能】「削除する」ボタン押下時処理
     *
     * 【概要】削除処理を行い成功の場合「画面遷移」、失敗の場合「ダイアログ表示」を行います
     *
     * 【作成日、作成者】2024/03/24 N.OONISHI
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun onClickDeleteButton() {
        // 映画情報を非同期処理で格納
        GlobalScope.launch {
            if (deleteData()) {
                // 格納成功時にメイン画面に遷移
                //moveNextScreen(M001MainActivity::class.java)
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
     * 【機能】「更新する」ボタン押下時処理
     *
     * 【概要】更新処理を行い成功の場合「画面遷移」、失敗の場合「ダイアログ表示」を行います
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun onClickUpdateButton() {
        // 入力チェックを行いNGの場合はダイアログを表示し、処理を中断
        if (!ValidationUtil.isInputCheck(dateEdt, directorEdt, titleEdt)) {
            showInputDialog()
            return
        }
        // 映画情報を非同期処理で格納
        GlobalScope.launch {
            if (updateData()) {
                // 格納成功時にメイン画面に遷移
                //moveNextScreen(M001MainActivity::class.java)
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
     * 【機能】削除処理
     *
     * 【概要】idをもとに映画情報を削除
     *
     * 【作成日、作成者】2024/03/24 N.OONISHI
     *
     * @return isCheck DB登録処理：成功(true) 失敗(false)
     */
    private suspend fun deleteData(): Boolean {
        val tmp = data.tmp
        // ビジネスロジックを呼び出し画面に入力された映画情報で更新
        val result = withContext(Dispatchers.IO) {
            BaseBusinessLogic().executeAsyncCallBack(
                applicationContext,
                Const.SQL_DELETE_MOVIE_INFO,
                tmp
            )
        }
        return result.isCheck
    }

    /**
     * 【機能】更新処理
     *
     * 【概要】idと入力された情報をもとに映画情報を更新
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     *
     * @return isCheck DB登録処理：成功(true) 失敗(false)
     */
    private suspend fun updateData(): Boolean {
        // 入力された文字列を取得
        val dateInput = dateEdt.text.toString()
        val directorInput = directorEdt.text.toString()
        val titleInput = titleEdt.text.toString()
        val tmp = data.tmp
        // ビジネスロジックを呼び出し画面に入力された映画情報で更新
        val result = withContext(Dispatchers.IO) {
            BaseBusinessLogic().executeAsyncCallBack(
                applicationContext,
                Const.SQL_UPDATE_MOVIE_INFO,
                dateInput,
                directorInput,
                titleInput,
                byteArray,
                tmp
            )
        }
        return result.isCheck
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.update_button ->
                    // 「更新する」ボタン押下時
                    onClickUpdateButton()
                R.id.delete_button ->
                    //  「削除しますか？」ダイアログを表示しOKボタン押下時に以下の処理を実行
                    showDeleteDialog(object : DialogResultCallback {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                onClickDeleteButton()
                            }
                        }
                    })
                R.id.date_picker_actions ->
                    // 日付選択ボタン押下時ダイアログを表示
                    DatePickerUtil.showDatePicker(dateEdt)
                R.id.imagebutton ->
                    selectImage()
            }
        }
    }
}
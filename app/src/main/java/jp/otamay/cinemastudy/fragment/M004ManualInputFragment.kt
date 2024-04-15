package jp.otamay.cinemastudy.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.activity.M001MainActivity
import jp.otamay.cinemastudy.activity.M008DisplayPerYearsActivity
import jp.otamay.cinemastudy.base.BaseBusinessLogic
import jp.otamay.cinemastudy.base.BaseFragment
import jp.otamay.cinemastudy.base.Const
import jp.otamay.cinemastudy.util.DatePickerUtil
import jp.otamay.cinemastudy.util.ValidationUtil
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

class M004ManualInputFragment: BaseFragment(), View.OnClickListener {

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

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.m004_fragment, container, false)
    }

    /** ActivityにおけるonCreate()に記述するコードをここに記載 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.input_button).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.date_picker_actions).setOnClickListener(this)
        imageButton = view.findViewById(R.id.imagebutton)
        imageButton.setOnClickListener(this)
        // ImageButtonの初期化
        // 画像未選択時の保存データ用に初期表示画像（NO IMAGE）をByteArray型で取得します
        byteArray = getImageResourceAsByteArray(requireContext(), R.drawable.ic_init_image)

        // UIの初期化
        dateEdt = view.findViewById(R.id.dateEdit)
        directorEdt = view.findViewById(R.id.directorEditText)
        titleEdt = view.findViewById(R.id.titleEditText)
        imageButton.setImageBitmap(byteArrayToBitmap(byteArray)?.let { resizeBitmap(it) })
        // キーパットからの入力制御
        dateEdt.isEnabled = false
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
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
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
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            val bitmap = BitmapFactory.decodeStream(imageUri?.let {
                requireContext().contentResolver.openInputStream(
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
     * 【機能】登録するボタン押下時処理
     *
     * 【概要】入力チェック後、DB登録処理を実行し成功時メイン画面へ遷移（失敗時ダイアログを表示）
     *
     *【作成日、作成者】2024/03/23 N.OONISHI
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun onClickInputButton() {
        // 入力チェックを行いNGの場合はダイアログを表示し、処理を中断
        if(!ValidationUtil.isInputCheck(dateEdt, directorEdt, titleEdt)){
            showInputDialog()
            return
        }
        // 映画情報を非同期処理で格納
        GlobalScope.launch {
            if (registerData()) {
                // 格納成功時にメイン画面に遷移
                moveNextScreen(M001MainActivity::class.java)
                activity?.finish()
            } else {
                withContext(Dispatchers.Main) {
                    // 格納失敗時にダイアログを表示
                    showErrorDialog(getString(R.string.error_dialog_title), getString(R.string.error_dialog_message))
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
    private suspend fun registerData(): Boolean{
        // 入力された文字列を取得
        val dateInput = dateEdt.text.toString()
        val directorInput = directorEdt.text.toString()
        val titleInput = titleEdt.text.toString()
        // ビジネスロジックを呼び出し画面に入力された映画情報を挿入
        val result = withContext(Dispatchers.IO){
            BaseBusinessLogic().executeAsyncCallBack(requireContext(), Const.SQL_INSERT_MOVIE_INFO, getTmp(), dateInput, directorInput, titleInput, byteArray)
        }
        return result.isCheck
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                // 「登録する」ボタン押下
                R.id.input_button ->
                    onClickInputButton()
                // 日付選択ボタン押下時
                R.id.date_picker_actions ->
                    // 日付選択ダイアログを表示
                    DatePickerUtil.showDatePicker(dateEdt)
                // 画像選択
                R.id.imagebutton ->
                    selectImage()
            }
        }
    }
}
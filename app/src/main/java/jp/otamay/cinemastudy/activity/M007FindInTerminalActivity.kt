package jp.otamay.cinemastudy.activity

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.base.BaseActivity
import jp.otamay.cinemastudy.base.BaseBusinessLogic
import jp.otamay.cinemastudy.base.Const
import jp.otamay.cinemastudy.data.MovieContainer
import jp.otamay.cinemastudy.util.MainCardViewAdapter
import kotlinx.coroutines.*

class M007FindInTerminalActivity : BaseActivity() {

    /** ラジオグループ */
    private lateinit var radioGroup: RadioGroup

    /** 「旧作」ラジオボタン */
    private lateinit var titleRadio: RadioButton

    /** 検索欄EditText */
    private lateinit var searchEditText: EditText

    /** クリアボタン */
    private lateinit var clearButton: ImageButton

    /** API取得情報表示リサイクルビュー */
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m007_activity)

        // 各UI部品初期化
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        radioGroup = findViewById(R.id.radioGroup)
        titleRadio = findViewById(R.id.title_radio)
    }

    override fun onStart() {
        super.onStart()

        setActionBar("検索")

        // 初期値：「タイトル」を選択状態に
        titleRadio.isChecked = true

        // 検索欄に入力された文字列のクリア処理
        clearButton.setOnClickListener {
            searchEditText.text.clear()
        }

        // 検索欄に入力された文字列をもとに検索を実行し、結果を画面に表示
        searchEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager =
                    this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // キーボード閉じる処理
                inputMethodManager.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
                // データ取得・表示
                fetchMovie(searchEditText.text.toString().trim())
                true
            }
            false
        }
    }

    /**
     * 【機能】検索処理分岐
     *
     * 【概要】保存映画情報を「タイトル」「監督名」で分岐・検索を行います
     *
     * 【作成日、作成者】2024/04/08 N.OONISHI
     *
     */
    private fun fetchMovie(query: String) {
        when (radioGroup.checkedRadioButtonId) {
            // 「タイトル」選択時
            R.id.title_radio -> handleMovieDisplay(query, Const.SQL_SELECT_BY_TITLE)
            // 「監督名」選択時
            R.id.director_radio -> handleMovieDisplay(query, Const.SQL_SELECT_BY_DIRECTOR)
        }
    }

    /**
     * 【機能】データ取得・表示
     *
     * 【概要】SQLとパラメータを指定しデータを取得・表示します
     *
     * 【作成日、作成者】2024/04/12 N.OONISHI
     *
     * @param query SQLに設定するパラメータ
     * @param sql 実行するSQL文
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun handleMovieDisplay(query: String, sql: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // 画面表示用のデータを取得
            val data = getDisplayData(query, sql)
            withContext(Dispatchers.Main) {
                // 取得した映画情報を画面に表示
                setupRecyclerView(recyclerView, data, ::createAdapter, ::onItemClick)
            }
        }
    }

    // カードビュークリックリスナーのコールバック
    private fun onItemClick(movieContainer: MovieContainer) {
        // クリックされたアイテムの処理を記述する
        moveNextScreen(M006EditActivity::class.java, movieContainer)
        finish()
    }

    // Adapter生成
    private fun createAdapter(data: List<MovieContainer>): MainCardViewAdapter {
        return createMainAdapter(data, ::onItemClick)
    }

    /**
     * 【機能】表示データ取得
     *
     * 【概要】画面に表示する映画情報を取得します
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     *
     * @param query　パラメータ
     * @param sql SQL文
     * @return result.movieEntity　全映画情報
     */
    private fun getDisplayData(query: String, sql: String): List<MovieContainer> {
        // ビジネスロジックを呼び出し画面に入力された映画情報を挿入
        val logic = BaseBusinessLogic()
        val result = logic.executeAsyncCallBack(this, sql, query)
        return result.movieEntity
    }

}
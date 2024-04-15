package jp.otamay.cinemastudy.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.base.BaseActivity
import jp.otamay.cinemastudy.base.BaseBusinessLogic
import jp.otamay.cinemastudy.base.Const
import jp.otamay.cinemastudy.base.Key
import jp.otamay.cinemastudy.data.MovieContainer
import jp.otamay.cinemastudy.util.MainCardViewAdapter
import kotlinx.coroutines.*

/**
 * 【クラス】年別表示画面
 *
 * 【機能】登録した映画情報を選択された年ごとに画面に表示します
 */
class M008DisplayPerYearsActivity : BaseActivity() {

    /** タップされたデータ */
    private lateinit var data: MovieContainer

    /** タップされた年 */
    private lateinit var initYear: String

    /** リサイクルビュー */
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m008_activity)

        // タップされたデータの取得
        data = intent.getSerializableExtra(Key.KEY_INTENT) as MovieContainer
    }

    override fun onStart() {
        super.onStart()
        // リサイクルビュー設定
        recyclerView = findViewById(R.id.recyclerView)
        // 選択した年
        initYear = data.year
        // 年文字列が取得できれば末尾に「年」を
        // 取得できなかった場合は「年別」を表示
        val yearOnBar = if (initYear.isNotEmpty()) {
            initYear + "年"
        } else {
            "年別"
        }
        // アクションバー設定
        setActionBar(yearOnBar)
        // 初期設定
        init()
    }

    //メニュー表示の為の関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        //メニューのリソース選択
        inflater.inflate(R.menu.m008_activity_menu, menu)
        return true
    }

    /**
     * 【機能】メニュー処理
     *
     * 【概要】メニューのアイテムを押下した時の処理
     *
     * 【作成日、作成者】2024/04/07 N.OONISHI
     *
     * 【更新日、更新者】
     *
     * @param item　メニュー項目
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_new -> handleShowItem(Const.SQL_SELECT_NEW_PER_YEAR)
            R.id.menu_old -> handleShowItem(Const.SQL_SELECT_OLD_PER_YEAR)
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 【機能】初期設定
     *
     * 【概要】画面表示等の初期設定を行います
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     *
     */
    private fun init() {
        // 初期表示映画情報を取得し、画面に表示
        handleShowItem(Const.SQL_SELECT_NEW_PER_YEAR)
    }

    /**
     * 【機能】並び替え表示データ取得
     *
     * 【概要】メニューの並び替え後の画面に表示する映画情報を取得します
     *
     * 【作成日、作成者】2024/03/23 N.OONISHI
     *
     * 【更新日、更新者】2024/04/08 N.OONISHI
     * 並び替え処理実装のためSQL文を指定するメソッドに変更
     *
     * @return 全映画情報
     */
    private fun getDisplayData(sql: String): List<MovieContainer> {
        // ビジネスロジックを呼び出し画面に入力された映画情報を挿入
        val logic = BaseBusinessLogic()
        val result = logic.executeAsyncCallBack(this, sql, initYear)
        return result.movieEntity
    }

    /**
     * 【機能】映画情報取得・表示
     *
     * 【概要】任意のSQLを指定し、画面に表示させたい映画情報を取得し画面に表示します
     *
     * 【作成日、作成者】2024/04/08 N.OONISHI
     *
     * 【更新日、更新者】
     *
     * @param sql　実行したいSQL文
     */
    private fun handleShowItem(sql: String): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            // 画面表示用データを取得
            val data = getDisplayData(sql)
            // UIスレッドに戻ってデータを表示する
            withContext(Dispatchers.Main) {
                // 画面項目設定
                setupRecyclerView(recyclerView, data, ::createAdapter, ::onItemClick)
            }
        }
        return true
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

}
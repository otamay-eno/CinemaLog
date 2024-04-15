package jp.otamay.cinemastudy.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.base.BaseActivity
import jp.otamay.cinemastudy.base.BaseBusinessLogic
import jp.otamay.cinemastudy.base.Const
import jp.otamay.cinemastudy.data.MovieContainer
import jp.otamay.cinemastudy.data.MovieStatistics
import jp.otamay.cinemastudy.db.MovieDataBase
import jp.otamay.cinemastudy.util.MainCardViewAdapter
import jp.otamay.cinemastudy.util.YearCardViewAdapter
import kotlinx.coroutines.*

/**
 * 【クラス】メイン画面
 *
 * 【機能】登録した映画情報を画面に表示します
 */
class M001MainActivity : BaseActivity(), View.OnClickListener {

    /** リサイクルビュー */
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m001_activity)
    }

    override fun onStart() {
        super.onStart()
        // リサイクルビュー設定
        recyclerView = findViewById(R.id.recyclerView)
        // ボタンリスナー設定
        findViewById<FloatingActionButton>(R.id.inputButton).setOnClickListener(this)
        // アクションバー設定
        setActionBar("ホーム")

        // 初期設定
        init()
    }

    //メニュー表示の為の関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        //メニューのリソース選択
        inflater.inflate(R.menu.m001_activity_menu, menu)
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
            R.id.menu_new -> handleShowItem(Const.SQL_SELECT_ALL_NEW)
            R.id.menu_old -> handleShowItem(Const.SQL_SELECT_ALL_OLD)
            R.id.menu_year -> sortByYear()
            R.id.menu_search -> {
                // 「検索」画面へ遷移
                moveNextScreen(M007FindInTerminalActivity::class.java)
                true
            }
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
        handleShowItem(Const.SQL_SELECT_ALL_NEW)
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
        val result = logic.executeAsyncCallBack(this, sql)
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
                setupRecyclerView(recyclerView, data, ::createMainAdapter, ::onItemClickMain)
            }
        }
        return true
    }

    // カードビュークリックリスナーのコールバック
    private fun onItemClickMain(movieContainer: MovieContainer) {
        // クリックされたアイテムの処理を記述する
        moveNextScreen(M006EditActivity::class.java, movieContainer)
    }

    // Adapter生成
    private fun createMainAdapter(data: List<MovieContainer>): MainCardViewAdapter {
        return createMainAdapter(data, ::onItemClickMain)
    }

    // 0409
    private fun sortByYear(): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            // Roomデータベースのインスタンスを取得
            val db = MovieDataBase.getInstance(applicationContext)
            // 任意のSQLを実行
            val data = db.movieDao().getMovieByYear()
            // UIスレッドに戻ってデータを表示する
            withContext(Dispatchers.Main) {
                // 画面項目設定
                setupRecyclerView(
                    recyclerView,
                    dataConversion(data),
                    ::createYearAdapter,
                    ::onItemClickYear
                )
            }
        }
        return true
    }

    // カードビュークリックリスナーのコールバック
    private fun onItemClickYear(movieContainer: MovieContainer) {
        // クリックされたアイテムの処理を記述する
        moveNextScreen(M008DisplayPerYearsActivity::class.java, movieContainer)
    }

    // Adapter生成
    private fun createYearAdapter(data: List<MovieContainer>): YearCardViewAdapter {
        return createYearAdapter(data, ::onItemClickYear)
    }

    private fun dataConversion(data: List<MovieStatistics>): List<MovieContainer> {
        return data.map { movieStatistics ->
            MovieContainer(
                "", "", "", "", ByteArray(0),
                "", "", movieStatistics.year, movieStatistics.movieCount
            )
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                // 登録画面へ
                R.id.inputButton -> {
                    moveNextScreen(M002RegisterParentActivity::class.java)
                }

            }
        }
    }
}
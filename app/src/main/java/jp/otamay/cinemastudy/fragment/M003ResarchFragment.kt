package jp.otamay.cinemastudy.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.activity.M005RegisterActivity
import jp.otamay.cinemastudy.api.RakutenService
import jp.otamay.cinemastudy.api.TMDBService
import jp.otamay.cinemastudy.base.BaseFragment
import jp.otamay.cinemastudy.data.*
import jp.otamay.cinemastudy.util.MovieApiCardViewAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class M003ResearchFragment : BaseFragment() {

    /** ラジオグループ */
    private lateinit var radioGroup: RadioGroup

    /** 「旧作」ラジオボタン */
    private lateinit var oldRadio: RadioButton

    /** 検索欄EditText */
    private lateinit var searchEditText: EditText

    /** クリアボタン */
    private lateinit var clearButton: ImageButton

    /** API取得情報表示リサイクルビュー */
    private lateinit var recyclerView: RecyclerView

    /** TmdbベースURL */
    private var baseTmdbUrl = "https://api.themoviedb.org/"

    /** 楽天ブックスDVD・Blu-ray検索APIベースURL */
    private var baseRakutenUrl = "https://app.rakuten.co.jp/services/api/"

    /** TmdbAPIキー */
    private val apiTmdbKey = "5b0fecbd77abc7753c4decc607d1626b"

    /** 楽天APIキー（applicationId） */
    private val rakutenTmdbKey = "1048091687950021213"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.m003_fragment, container, false)
    }

    /** ActivityにおけるonCreate()に記述するコードをここに記載 */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        clearButton = view.findViewById(R.id.clearButton)

        radioGroup = view.findViewById(R.id.radioGroup)
        oldRadio = view.findViewById(R.id.old_radio)
    }

    /** ActivityのonStart()と同じ */
    override fun onStart() {
        super.onStart()

        // 検索欄に入力された文字列のクリア処理
        clearButton.setOnClickListener {
            searchEditText.text.clear()
        }

        // 初期値：「旧作」を選択状態に
        oldRadio.isChecked = true

        searchEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // キーボード閉じる処理
                inputMethodManager.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
                // 実行したい処理 START
                fetchMovie(searchEditText.text.toString().trim())
                // 実行したい処理 END
                true
            }
            false
        }
    }

    /**
     * 【機能】API実行分岐
     *
     * 【概要】楽天・TmdbのAPI実行を分岐させます
     *
     * 【作成日、作成者】2024/04/08 N.OONISHI
     *
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchMovie(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            when (radioGroup.checkedRadioButtonId) {
                R.id.old_radio -> {
                    executeRakutenApi(query)
                }
                R.id.new_radio -> {
                    executeTmdbApi(query)
                }
            }
        }
    }

    // 04/04
    private fun executeRakutenApi(query: String) {
        // TMDB APIのリクエストを送信
        val retrofit = Retrofit.Builder()
            .baseUrl(baseRakutenUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val rakutenService = retrofit.create(RakutenService::class.java)

        val call = rakutenService.searchMovies(rakutenTmdbKey, query)
        call.enqueue(object : Callback<RakutenApiResponse> {
            override fun onResponse(
                call: Call<RakutenApiResponse>,
                response: Response<RakutenApiResponse>
            ) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    movieDetails?.let {
                        // レスポンス内からMovie型の結果を取得
                        val resultList: List<RakutenApiDetail> = movieDetails.items
                        // リストの初期化
                        val list = mutableListOf<MovieContainer>()
                        for (element in resultList) {
                            val item = element.item
                            val movieItem = MovieContainer(
                                originalTitle = item.title,
                                director = returnDirectorNm(item.artistName),
                                posterPath = item.largeImageUrl,
                                tmp = "",
                                title = "",
                                date = "",
                                image = ByteArray(0),
                                year = "",
                                movieCount = 0
                            )
                            list.add(movieItem)
                        }
                        setScreen(list)
                    }
                }
            }

            override fun onFailure(call: Call<RakutenApiResponse>, t: Throwable) {
                // リクエストが失敗した場合の処理
                showErrorDialog(
                    getString(R.string.connection_error_dialog_title),
                    getString(R.string.connection_error_dialog_message)
                )
            }
        })
    }

    private fun executeTmdbApi(query: String) {
        // TMDB APIのリクエストを送信
        val retrofit = Retrofit.Builder()
            .baseUrl(baseTmdbUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmdbService = retrofit.create(TMDBService::class.java)

        val call = tmdbService.searchMovies(apiTmdbKey, query)

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    movieDetails?.let {
                        // レスポンス内からMovie型の結果を取得
                        val resultList: List<MovieDetails> = movieDetails.results
                        // リストの初期化
                        val list = mutableListOf<MovieContainer>()
                        for (element in resultList) {
                            val movieItem = MovieContainer(
                                originalTitle = element.originalTitle,
                                director = "",
                                posterPath = createPosterUrl(element.posterPath),
                                tmp = "",
                                title = "",
                                date = "",
                                image = ByteArray(0),
                                year = "",
                                movieCount = 0
                            )
                            list.add(movieItem)
                        }
                        setScreen(list)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                // リクエストが失敗した場合の処理
                showErrorDialog(
                    getString(R.string.connection_error_dialog_title),
                    getString(R.string.connection_error_dialog_message)
                )
            }
        })
    }

    /**
     * 【機能】画面項目設定
     *
     * 【概要】画面に表示する項目を設定します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * @param displayData 画面表示データ
     */
    private fun setScreen(data: List<MovieContainer>) {
        // 映画情報をリサイクルビューに設定
        setupRecyclerView(recyclerView, data)
    }

    /**
     * 【機能】画面項目設定
     *
     * 【概要】画面に表示する項目を設定します
     *
     * 【作成日、作成者】2024/03/21 N.OONISHI
     *
     * @param recyclerView 表示先リサイクルビュー
     * @param data　表示させたいデータ
     */
    private fun setupRecyclerView(recyclerView: RecyclerView, data: List<MovieContainer>) {
        // CustomCardViewAdapterを生成
        val adapter = MovieApiCardViewAdapter(data)
        // クリックリスナーを設定
        adapter.setOnItemClickListener { movieContainer ->
            moveNextScreen(M005RegisterActivity::class.java, movieContainer)
        }
        // RecyclerViewにAdapterをセット
        recyclerView.adapter = adapter
        // レイアウトマネージャーを設定
        //recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * 【機能】ポスター画像URL作成
     *
     * 【概要】TmdbAPIで取得したポスター画像URLを基に表示用URLを作成します
     *
     * 【作成日、作成者】2024/04/06 N.OONISHI
     *
     * @return url 表示用URL
     */
    private fun createPosterUrl(basePosterUrl: String?): String {
        return StringBuffer().append("https://image.tmdb.org/t/p/w185").append(basePosterUrl)
            .toString()
    }
}







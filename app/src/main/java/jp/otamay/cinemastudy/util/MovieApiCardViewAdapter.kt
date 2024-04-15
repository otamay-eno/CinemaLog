package jp.otamay.cinemastudy.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.data.MovieContainer

// CustomCardViewAdapterクラス
class MovieApiCardViewAdapter(private val data: List<MovieContainer>) : RecyclerView.Adapter<MovieApiCardViewAdapter.ViewHolder>() {

    // クリックリスナー
    private var onItemClickListener: ((MovieContainer) -> Unit)? = null

    // ViewHolderクラス
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 各要素のView
        val titleTv: TextView = itemView.findViewById(R.id.title)
        val directorTv: TextView = itemView.findViewById(R.id.director)
        val imageView: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // CardViewのレイアウトをinflate
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_api, parent, false)
        // CardViewの設定
        setCardView(view.findViewById(R.id.card))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 各要素のテキスト設定
        holder.titleTv.text = data[position].originalTitle
        var directorName = data[position].director
        if (directorName.equals("")) {
            directorName = " "
        }
        holder.directorTv.text = directorName
        // ポスター画像のパス取得
        val path = data[position].posterPath
        // 画像をロードして表示
        //あらかじめ表示オプションを指定しておく
        val option: RequestOptions = RequestOptions().let {
            //ここでキャッシュの有無、ローディング中の画像、画像サイズなどを指定できる
            it.fitCenter()
        }
        //withでimageViewがあるActivityかFragmentを指定する
        //loadで画像のダウンロード先を指定する
        //intoで表示させるimageViewを指定する
        Glide.with(holder.imageView).load(path).apply(option).into(holder.imageView)
        // クリックリスナーの設定
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(data[position])
        }
    }

    override fun getItemCount(): Int {
        // データ件数を取得
        return data.size
    }

    // クリックリスナーのセット
    fun setOnItemClickListener(listener: (MovieContainer) -> Unit) {
        onItemClickListener = listener
    }

    /**
     * 【機能】カードビュー設定
     *
     * 【概要】カードビューの丸み、影の設定を行います
     *
     * 【作成日、作成者】2024/04/05 N.OONISHI
     *
     * @param view カードビュー
     */
    private fun setCardView(view: CardView) {
        view.radius = 40f
        view.cardElevation = 20f
    }
}



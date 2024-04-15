package jp.otamay.cinemastudy.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.data.MovieContainer

// CustomCardViewAdapterクラス
class YearCardViewAdapter(private val data: List<MovieContainer>) : RecyclerView.Adapter<YearCardViewAdapter.ViewHolder>() {

    // クリックリスナー
    private var onItemClickListener: ((MovieContainer) -> Unit)? = null

    // ViewHolderクラス
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 各要素のView
        val year: TextView = itemView.findViewById(R.id.date)
        val count: TextView = itemView.findViewById(R.id.count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // CardViewのレイアウトをinflate
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_year, parent, false)
        // CardViewの設定
        setCardView(view.findViewById(R.id.card))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 各要素のテキスト設定
        holder.year.text = data[position].year + "年"
        holder.count.text = data[position].movieCount.toString() + "本"

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
        view.radius = 45f
        view.cardElevation = 20f
    }
}



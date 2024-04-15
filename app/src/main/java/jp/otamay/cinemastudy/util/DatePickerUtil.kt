package jp.otamay.cinemastudy.util

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.widget.EditText
import androidx.annotation.RequiresApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object DatePickerUtil {

    private const val DATE_FORMAT = "yyyy/MM/dd"

    /**
     * 【機能】日付選択ダイアログを表示する
     * 【概要】指定されたEditTextに関連付けられた日付選択ダイアログを表示し、ユーザーが日付を選択するとEditTextにその日付を設定します。
     * 【作成日・作成者】年/月/日 作成者名
     *
     * @param eText 日付を入力または表示するEditText
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePicker(eText: EditText) {
        // 日付入力フィールドが空白など日付ではないときの初期値として、「今日」を設定
        val defaultDate = LocalDate.now()
        var year = defaultDate.year
        var monthValue = defaultDate.monthValue
        var dayOfMonth = defaultDate.dayOfMonth

        // 日付入力フィールドの値が日付の場合はその値を設定
        val localDate = toLocaleDate(eText.text.toString())
        if (localDate != null) {
            year = localDate.year
            monthValue = localDate.monthValue
            dayOfMonth = localDate.dayOfMonth
        }

        // ドラム式DatePicker表示
        val picker = DatePickerDialog(
            eText.context,
            AlertDialog.THEME_HOLO_LIGHT,   // テーマ：ドラム式 背景白

            // ダイアログでOKをクリックされたときの処理 日付入力フィールドへ値を設定
            DatePickerDialog.OnDateSetListener { view, getYear, getMonthOfYear, getDayOfMonth ->
                eText.setText(String.format(Locale.US, "%d/%02d/%02d",
                    getYear,
                    getMonthOfYear + 1,     // 月はZEROオリジン
                    getDayOfMonth))
            },

            // DatePickerが初期表示する日付
            year,
            monthValue - 1, // 月はZEROオリジン
            dayOfMonth
        )
        picker.setCanceledOnTouchOutside(false)
        picker.show()
    }

    /**
     * 【機能】<br>文字列をローカル日付に変換する<br>
     * 【概要】<br>指定された文字列形式の日付をローカルの日付オブジェクトに変換します<br>
     * 【作成日・作成者】<br>引用のため不記載 年/月/日 作成者名<br>
     *
     * @param stringDate 変換したい日付の文字列形式（指定されたフォーマットに準拠する必要があります）
     * @return 変換されたローカル日付オブジェクト。変換に失敗した場合はnullを返します
     */
    fun toLocaleDate(stringDate: String): LocalDate? {
        // 指定された日付フォーマットに基づいてDateTimeFormatterを生成
        val df = DateTimeFormatter.ofPattern(DATE_FORMAT)
        return try {
            // 指定された文字列形式の日付をDateオブジェクトに変換
            val date = SimpleDateFormat(DATE_FORMAT, Locale.US).parse(stringDate)
            // Calendarオブジェクトを生成し、変換されたDateオブジェクトを設定
            val calendar = Calendar.getInstance()
            calendar.time = date
            // Calendarオブジェクトから年月日を取得し、それを元にLocalDateオブジェクトを生成して返す
            LocalDate.of(
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.DAY_OF_MONTH]
            )
        } catch (e: ParseException) {
            // 変換に失敗した場合はnullを返す
            null
        }
    }
}

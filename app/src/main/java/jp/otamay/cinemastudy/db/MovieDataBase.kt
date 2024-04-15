package jp.otamay.cinemastudy.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class], version = 15, exportSchema = false)
abstract class MovieDataBase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDataBase? = null

        fun getInstance(context: Context): MovieDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDataBase::class.java,
                    "database-name"
                )
                    .fallbackToDestructiveMigration() // 新しいバージョンのスキーマに更新時、データベースを破棄して再構築する
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


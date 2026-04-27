package jobplace.courseproject.monitorstorm.data.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "storm_db"
            ).build().also { INSTANCE = it }
        }
    }
}
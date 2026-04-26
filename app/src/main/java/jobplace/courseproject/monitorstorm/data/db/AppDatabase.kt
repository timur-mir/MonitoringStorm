package jobplace.courseproject.monitorstorm.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [KpEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kpDao(): KpDao
}
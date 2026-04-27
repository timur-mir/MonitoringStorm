package jobplace.courseproject.monitorstorm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<KpEntity>)

    @Query("SELECT * FROM kp_index ORDER BY time DESC LIMIT 20")
    fun getLatest(): Flow<List<KpEntity>>

    @Query("SELECT * FROM kp_index ORDER BY time DESC LIMIT 1")
    suspend fun getLatestOne(): KpEntity?
}
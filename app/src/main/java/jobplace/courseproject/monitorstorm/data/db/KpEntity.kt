package jobplace.courseproject.monitorstorm.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kp_index")
data class KpEntity(
    @PrimaryKey val time: String,
    val value: Double
)
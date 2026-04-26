package jobplace.courseproject.monitorstorm.data.repository

import jobplace.courseproject.monitorstorm.data.api.SpaceWeatherApi
import jobplace.courseproject.monitorstorm.data.db.KpDao
import jobplace.courseproject.monitorstorm.data.db.KpEntity
import jobplace.courseproject.monitorstorm.data.model.KpIndex

class StormRepository(
    private val api: SpaceWeatherApi,
    private val dao: KpDao
) {

    suspend fun refresh() {
        val data = api.getKp().takeLast(20)

        val entities = data.map {
            KpEntity(it.time_tag, it.kp_index)
        }

        dao.insertAll(entities)
    }

    fun observe() = dao.getLatest()
}

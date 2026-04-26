package jobplace.courseproject.monitorstorm.data.api

import jobplace.courseproject.monitorstorm.data.model.KpIndex
import retrofit2.http.GET

interface SpaceWeatherApi {
    @GET("planetary_k_index_1m.json")
    suspend fun getKp(): List<KpIndex>
}
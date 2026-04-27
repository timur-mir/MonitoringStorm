package jobplace.courseproject.monitorstorm.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    val api: SpaceWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://services.swpc.noaa.gov/json/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpaceWeatherApi::class.java)
    }
}
package com.example.gdgandroidwebinar6.api

import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("api/location/{locationId}/")
    suspend fun getWeather(@Path("locationId") locationId: Int): Weather
}

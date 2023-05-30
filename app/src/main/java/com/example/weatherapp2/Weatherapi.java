package com.example.weatherapp2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weatherapi {
    @GET("weather")
    Call<WeatherResponse> getWeather(@Query("q") String cityName,
                          @Query("appid") String api_key);
}

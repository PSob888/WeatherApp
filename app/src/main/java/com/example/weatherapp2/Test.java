package com.example.weatherapp2;

import com.google.gson.annotations.SerializedName;

public class Test {
    @SerializedName("weather")
    Weather weather;

    public Weather getWeather(){
        return weather;
    }

    public void setWeather(Weather weather){
        this.weather = weather;
    }
}

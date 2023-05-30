package com.example.weatherapp2;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Date;

public class WeatherPanel {
    @SerializedName("weatherResponse")
    WeatherResponse weatherResponse;
    @SerializedName("updateDate")
    Date updateDate;

    public WeatherPanel(WeatherResponse weather, Date updateDate) {
        this.weatherResponse = weather;
        this.updateDate = updateDate;
    }

    public WeatherResponse getWeatherResponse() {
        return weatherResponse;
    }

    public void setWeatherResponse(WeatherResponse weather) {
        this.weatherResponse = weather;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}

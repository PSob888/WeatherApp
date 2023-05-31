package com.example.weatherapp2;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class WeatherPanel {
    @SerializedName("weatherResponse")
    WeatherResponse weatherResponse;
    @SerializedName("updateDate")
    Date updateDate;
    @SerializedName("temps")
    List<Double> temps;

    public WeatherPanel(WeatherResponse weather, Date updateDate, List<Double> temps) {
        this.weatherResponse = weather;
        this.updateDate = updateDate;
        this.temps = temps;
    }

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

    public List<Double> getTemps() {
        return temps;
    }

    public void setTemps(List<Double> temps) {
        this.temps = temps;
    }
}

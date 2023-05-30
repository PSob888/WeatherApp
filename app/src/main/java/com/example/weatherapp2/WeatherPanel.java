package com.example.weatherapp2;

import java.util.Date;

public class WeatherPanel {
    WeatherResponse weather;
    Date updateDate;

    public WeatherPanel(WeatherResponse weather, Date updateDate) {
        this.weather = weather;
        this.updateDate = updateDate;
    }

    public WeatherResponse getWeather() {
        return weather;
    }

    public void setWeather(WeatherResponse weather) {
        this.weather = weather;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}

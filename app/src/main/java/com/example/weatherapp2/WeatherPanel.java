package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public void refreshData(MainActivity mainActivity, String name){
        Log.d("MyTag", "Przed1 " + String.valueOf(weatherResponse.getDt()));
        getWeather(mainActivity, name);
        Log.d("MyTag", "Po1 " + String.valueOf(weatherResponse.getDt()));
    }

    public void getWeather(MainActivity mainActivity, String name){
        String api_key = "72f43f52d4476175f100769b6a0ab5cd";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi myApi = retrofit.create(Weatherapi.class);

        String cityName = weatherResponse.getName();

        Call<WeatherResponse> testCall = myApi.getWeather(cityName, api_key);
        testCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.code() == 404){
                    Log.d("MyTag", "Cos sie popsulo 3");
                }
                else if(!(response.isSuccessful())){
                    Log.d("MyTag", "Cos sie popsulo 4");
                }
                else{
                    WeatherResponse myData = response.body();

                    if(myData.getCoord() == null){
                    }
                    else{
                        Log.d("MyTag", "Przed2 " + String.valueOf(weatherResponse.getDt()));
                        setWeatherResponse(myData);
                        Log.d("MyTag", "Po2 " + String.valueOf(weatherResponse.getDt()));
                        setUpdateDate(Calendar.getInstance().getTime());
                        getForecast(mainActivity, name);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d("MyTag", "Weather " + t.getMessage());
            }
        });

    }

    public void getForecast(MainActivity mainActivity, String name){
        String api_key = "72f43f52d4476175f100769b6a0ab5cd";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi myApi = retrofit.create(Weatherapi.class);
        String cityName = weatherResponse.getName();

        Call<WeatherData> testCall = myApi.getForecast(cityName, api_key);
        testCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful()) {
                    WeatherData weatherData = response.body();
                    List<WeatherItem> dataList = weatherData.getList();

                    List<Double> doubles = new ArrayList<>();
                    for (WeatherItem weatherItem : dataList) {
                        doubles.add(weatherItem.getMain().getTemp());
                    }

                    List<Double> temps = new ArrayList<>();
                    for(int i=0; i<5; i++){
                        Double temp = doubles.get((i*8)) + doubles.get((i*8) + 1)
                                + doubles.get((i*8) + 2) + doubles.get((i*8) + 3)
                                + doubles.get((i*8) + 4) + doubles.get((i*8) + 5)
                                + doubles.get((i*8) + 6) + doubles.get((i*8) + 7);
                        temp = temp / 8.0;
                        temp = round(temp, 1);
                        temps.add(i,temp);
                    }

                    setTemps(temps);

                    WeatherPanel nowy = new WeatherPanel(weatherResponse, updateDate, temps);

                    SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = gson.toJson(nowy, WeatherPanel.class);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString(name, json);
                    prefsEditor.commit();

                    mainActivity.getViewPager().setCurrentItem(mainActivity.getViewPager().getCurrentItem());

                    Log.d("MyTag", "Data should be refreshed");

                } else {
                    Log.d("MyTag", "cos sie popsulo2");
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                //Toast.makeText(getActivity() , t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("MyTag", "Forecast " + t.getMessage());
            }
        });
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

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}

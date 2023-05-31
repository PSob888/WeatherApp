package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdaterThread extends Thread {
    private volatile boolean isRunning = true;
    String api_key = "72f43f52d4476175f100769b6a0ab5cd";
    WeatherPanel weatherPanel;

    private MainActivity mainActivity;

    public UpdaterThread(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        isRunning = true;
        Log.d("MyTag", "thread started");
        while (isRunning) {
            long timeToRefresh = getTimeTorefresh();
            //refreshing current
            weatherPanel = getWeatherPanel("current");
            Log.d("MyTag", weatherPanel.getUpdateDate().toString());
            Date data = new Date();
            Date diff = new Date(data.getTime() - weatherPanel.getUpdateDate().getTime());
            Log.d("MyTag", String.valueOf(diff.getTime()));
            long modifier = 1000*60*timeToRefresh;
            Log.d("MyTag", String.valueOf(modifier));
            if(diff.getTime() >= modifier){
                Log.d("MyTag", "Modifying current");
                String cityName = weatherPanel.getWeatherResponse().getName();
                Log.d("MyTag", cityName);
                getWeather(cityName);
                getForecast(cityName);
                Log.d("MyTag", weatherPanel.getUpdateDate().toString());
                setWeatherPanel("current");
            }

            //refreshing favourites
            List<String> list = mainActivity.getFavourites();
            for(int i = 0; i < list.size(); i++){
                weatherPanel = getWeatherPanel(list.get(i));
                data = new Date();
                diff = new Date(data.getTime() - weatherPanel.getUpdateDate().getTime());
                if(diff.getTime() >= modifier){
                    String cityName = weatherPanel.getWeatherResponse().getName();
                    getWeather(cityName);
                    getForecast(cityName);
                    setWeatherPanel(list.get(i));
                }
            }

            //sleep for 20 seconds
            try {
                sleep(1000*20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d("MyTag", "Thread stopped");
    }

    public void stopThread() {
        isRunning = false;
    }

    public int getTimeTorefresh(){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("settings", "");
        Settings settings1 = gson.fromJson(json, Settings.class);
        return settings1.getRefreshTime();
    }

    public WeatherPanel getWeatherPanel(String filename){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(filename, "");
        WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);
        return weatherPanel;
    }

    public void setWeatherPanel(String filename){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(weatherPanel, WeatherPanel.class);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(filename, json);
        prefsEditor.commit();
    }

    public void getWeather(String cityName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi myApi = retrofit.create(Weatherapi.class);

        Call<WeatherResponse> testCall = myApi.getWeather(cityName, api_key);
        testCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.code() == 404){
                    Log.d("MyTag", "Dupa");
                }
                else if(!(response.isSuccessful())){
                    Log.d("MyTag", "Dupa");
                }
                else{
                    WeatherResponse myData = response.body();

                    if(myData.getCoord() == null){
                        Log.d("MyTag", "Dupa");
                    }
                    else{
                        WeatherPanel weatherPanel1 = new WeatherPanel(new WeatherResponse(myData), Calendar.getInstance().getTime());
                        saveWeatherPanel(weatherPanel1);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                //nic
                Log.d("MyTag", t.getMessage());
            }
        });

    }

    public void getForecast(String cityName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi myApi = retrofit.create(Weatherapi.class);

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
                    //weatherPanel.setTemps(temps);
                    WeatherPanel weatherPanel1 = new WeatherPanel(new WeatherResponse(weatherPanel.getWeatherResponse()), Calendar.getInstance().getTime(), temps);
                    saveWeatherPanel(weatherPanel1);
                } else {
                    Log.d("MyTag", "Dupa");
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                //nic
                Log.d("MyTag", t.getMessage());
            }
        });
    }

    public void saveWeatherPanel(WeatherPanel weatherPanel){
        this.weatherPanel = weatherPanel;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}

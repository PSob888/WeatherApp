package com.example.weatherapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    List<String> favourites;
    boolean isForeground;
    Timer timer;
    String api_key = "72f43f52d4476175f100769b6a0ab5cd";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveFavourites();
        refreshAllData();

        viewPager = findViewById(R.id.viewpager);
        PageAdapter pageAdapter = new PageAdapter(this);
        viewPager.setAdapter(pageAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        retrieveFavourites();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isForeground = true;
        recreateTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isForeground = false;
        pauseTimer();
    }

    public void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void recreateTimer() {
        if (isForeground && timer == null) {
            SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString("settings", "");
            Settings settings1 = gson.fromJson(json, Settings.class);
            long refreshTime = settings1.getRefreshTime();
            long period = refreshTime * 1000 * 60;
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    refreshAllData();
                }
            }, 0, period);
        }
    }

    public void refreshAllData(){
        SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("settings", "");
        Settings settings = gson.fromJson(json, Settings.class);

        long timeToRefresh = settings.getRefreshTime();

        gson = new Gson();
        json = mPrefs.getString("current", "");
        WeatherPanel current = gson.fromJson(json, WeatherPanel.class);

        Date data = new Date();
        Date diff = new Date(data.getTime() - current.getUpdateDate().getTime());
        long modifier = 1000*60*timeToRefresh;

        //refresh current
        if(diff.getTime() >= modifier){
            Log.d("MyTag", "Przed weather " + String.valueOf(current.getWeatherResponse().getDt()));
            getWeather("current", current, this);
            gson = new Gson();
            json = mPrefs.getString("current", "");
            current = gson.fromJson(json, WeatherPanel.class);
            Log.d("MyTag", "Po weather " + String.valueOf(current.getWeatherResponse().getDt()));
            getForecast("current", current, this);
        }

        //refresh favourites
        for(int i=0; i<favourites.size(); i++){
            String name = favourites.get(i);
            gson = new Gson();
            json = mPrefs.getString(name, "");
            WeatherPanel fav = gson.fromJson(json, WeatherPanel.class);

            Date datafav = new Date();
            Date difffav = new Date(datafav.getTime() - current.getUpdateDate().getTime());

            if(difffav.getTime() >= modifier){
                getWeather(name, fav, this);
                gson = new Gson();
                json = mPrefs.getString(name, "");
                fav = gson.fromJson(json, WeatherPanel.class);
                getForecast(name, fav, this);
            }
        }

    }

    public void getWeather(String cityName, WeatherPanel wp, MainActivity mainActivity){
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
                    //nic
                }
                else if(!(response.isSuccessful())){
                    //nic
                }
                else{
                    WeatherResponse myData = response.body();

                    if(myData.getCoord() == null){
                        //nic
                    }
                    else{

                        WeatherPanel test = new WeatherPanel(myData, Calendar.getInstance().getTime(), wp.getTemps());

                        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = gson.toJson(test, WeatherPanel.class);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putString(cityName, json);
                        prefsEditor.commit();


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

    public void getForecast(String cityName, WeatherPanel wp, MainActivity mainActivity){
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
                    wp.setTemps(temps);

                    SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = gson.toJson(wp, WeatherPanel.class);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString(cityName, json);
                    prefsEditor.commit();
                } else {
                    //nic
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                //nic
                Log.d("MyTag", t.getMessage());
            }
        });
    }

    public void saveFavourites(){
        SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
        String joinedString = TextUtils.join(",", favourites);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("favourites", joinedString);
        prefsEditor.commit();
    }

    public void retrieveFavourites(){
        SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
        String savedString = mPrefs.getString("favourites", "");
        setFavourites(new ArrayList<>(Arrays.asList(savedString.split(","))));
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void onClickSearch(View v){
        viewPager.setCurrentItem(0);
    }

    public void onClickFav(View v){
        viewPager.setCurrentItem(1);
    }

    public void onClickMenu(View v){
        viewPager.setCurrentItem(2);
    }

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }
}
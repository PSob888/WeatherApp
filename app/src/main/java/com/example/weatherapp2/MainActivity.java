package com.example.weatherapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        internetConnectionCheck();
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

    public void internetConnectionCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (!isConnected) {
            Toast.makeText(this , "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void recreateTimer() {
        if (isForeground && timer == null) {
            SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString("settings", "");
            Settings settings1 = gson.fromJson(json, Settings.class);
            long refreshTime = settings1.getRefreshTime();
            //long period = refreshTime * 1000 * 60;
            long period = 1 * 1000 * 60;
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    refreshAllData();
                    Log.d("MyTag", "test");
                }
            }, 0, period);
            Log.d("MyTag", "timer start");
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
            Log.d("MyTag", "refreshing");
            current.refreshData(this, "current");
        }

        //refresh favourites
        for(int i=0; i<favourites.size(); i++){
            String name = favourites.get(i);
            gson = new Gson();
            json = mPrefs.getString(name, "");
            WeatherPanel fav = gson.fromJson(json, WeatherPanel.class);

            Date datafav = new Date();
            Date difffav = new Date(datafav.getTime() - fav.getUpdateDate().getTime());

            if(difffav.getTime() >= modifier){
                Log.d("MyTag", "refreshing");
                fav.refreshData(this, name);
            }
        }

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

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public void setViewPagerItem(int position){
        viewPager = findViewById(R.id.viewpager);
        PageAdapter pageAdapter = new PageAdapter(this);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(position);
    }
}
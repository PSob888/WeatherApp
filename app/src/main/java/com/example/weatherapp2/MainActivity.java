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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    List<String> favourites;
    boolean isForeground;
    Timer timer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveFavourites();

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
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

//    public void pauseTimer() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }
//
//    public void recreateTimer() {
//        if (isForeground && timer == null) {
//            SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
//            Gson gson = new Gson();
//            String json = mPrefs.getString("settings", "");
//            Settings settings1 = gson.fromJson(json, Settings.class);
//            long refreshTime = settings1.getRefreshTime();
//            long period = refreshTime * 1000 * 60;
//            Timer timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    dataRefresh();
//                }
//            }, 20, period);
//        }
//    }

    public void refreshAllData(){
        SharedPreferences mPrefs = this.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("settings", "");
        WeatherPanel data = gson.fromJson(json, WeatherPanel.class);

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
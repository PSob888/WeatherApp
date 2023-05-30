package com.example.weatherapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    List<String> favourites;

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
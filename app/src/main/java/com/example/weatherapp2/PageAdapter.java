package com.example.weatherapp2;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PageAdapter extends FragmentStateAdapter {

    FragmentWeather fragmentWeather;
    FragmentFavourite fragmentFavourite;
    FragmentSettings fragmentSettings;

    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //if(fragmentFavourite == null || fragmentSettings == null || fragmentWeather == null)
        switch(position){
            case 0:
                return new FragmentWeather();
            case 1:
                return new FragmentFavourite();
            case 2:
                return new FragmentSettings();
            default:
                throw new Resources.NotFoundException("Position not found");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

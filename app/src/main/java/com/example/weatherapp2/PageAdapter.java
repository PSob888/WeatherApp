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

    private FragmentActivity mainActivity;
    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.mainActivity = fragmentActivity;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        EditText editText = mainActivity.findViewById(R.id.editTextTextPersonName);
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        switch(position){
            case 0:
                editText.setBackgroundColor(0x20FFFFFF);
                imageSearch.setBackgroundColor(0x20FFFFFF);
                imageFav.setBackgroundColor(0x00FFFFFF);
                imageMenu.setBackgroundColor(0x00FFFFFF);
                return new FragmentWeather();
            case 1:
                editText.setBackgroundColor(0x00FFFFFF);
                imageSearch.setBackgroundColor(0x00FFFFFF);
                imageFav.setBackgroundColor(0x20FFFFFF);
                imageMenu.setBackgroundColor(0x00FFFFFF);
                return new FragmentFavourite();
            case 2:
                editText.setBackgroundColor(0x00FFFFFF);
                imageSearch.setBackgroundColor(0x00FFFFFF);
                imageFav.setBackgroundColor(0x00FFFFFF);
                imageMenu.setBackgroundColor(0x20FFFFFF);
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

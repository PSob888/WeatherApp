package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavourite extends Fragment {

    private MainActivity mainActivity;
    Settings settings;

    List<FragmentContainerView> fragmentContainerViews = new ArrayList<>();

    public FragmentFavourite() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        getNewestSettings();
        //createNewFavs();
    }

    @Override
    public void onResume(){
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        imageSearch.setBackgroundColor(0x00FFFFFF);
        imageFav.setBackgroundColor(0x20FFFFFF);
        imageMenu.setBackgroundColor(0x00FFFFFF);
        super.onResume();
        getNewestSettings();
        clearFavs();
        createNewFavs();
    }

    public void createNewFavs(){
        int numContainers = mainActivity.getFavourites().size();
        LinearLayout linearLayout = getView().findViewById(R.id.linLayout);

        for (int i = 0; i < numContainers; i++) {
            FragmentManager fragmentManager = getChildFragmentManager(); // For Activity, use getFragmentManager()
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FrameLayout frameLayout = new FrameLayout(getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setId(i+500);

            String cityName = mainActivity.getFavourites().get(i);
            String temp = getTemp(cityName);
//            Log.d("MyTag", cityName);
//            Log.d("MyTag", temp);
            frameLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = mPrefs.getString(cityName, "");
                    WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);
                    json = gson.toJson(weatherPanel, WeatherPanel.class);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("current", json);
                    prefsEditor.commit();
                    mainActivity.viewPager.setCurrentItem(0);
                }
            });
            FragmentOneFavourite ff = FragmentOneFavourite.newInstance(cityName, temp);



            fragmentTransaction.add(i+500, ff);
            fragmentTransaction.commit();

            linearLayout.addView(frameLayout);
        }
    }

    public void clearFavs(){
        LinearLayout linearLayout = getView().findViewById(R.id.linLayout);
        linearLayout.removeAllViewsInLayout();
    }

    public void getNewestSettings(){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("settings", "");
        Settings settings1 = gson.fromJson(json, Settings.class);

        if(settings1 != null){
            settings = new Settings(settings1.isUseImperial(), settings1.getRefreshTime());
        }
        else{
            settings = new Settings(false, 5);
        }
    }

    public String getTemp(String cityName){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(cityName, "");
        WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);
        getNewestSettings();

        Double temp;
        String tempe = "";
        if(!settings.isUseImperial()){
            temp = round(weatherPanel.getWeatherResponse().getMain().getTemp()-273.15, 1);
            tempe += temp.toString();
            tempe += " *C";
        }
        else{
            temp = round(((weatherPanel.getWeatherResponse().getMain().getTemp()-273.15) * 9/5) + 32, 1);
            tempe += temp.toString();
            tempe += " *F";
        }
        return tempe;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
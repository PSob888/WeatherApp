package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

public class FragmentSettings extends Fragment {

    private FragmentActivity mainActivity;
    Settings settings;
    Switch switchImperial;
    Switch switchMinutes5;
    Switch switchMinutes10;
    Switch switchMinutes20;
    Switch switchMinutes30;
    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        switchImperial = view.findViewById(R.id.switch1);
        switchMinutes5 = view.findViewById(R.id.switchMinutes5);
        switchMinutes10 = view.findViewById(R.id.switchMinutes10);
        switchMinutes20 = view.findViewById(R.id.switchMinutes20);
        switchMinutes30 = view.findViewById(R.id.switchMinutes30);

        switchImperial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {
                    settings.setUseImperial(true);
                    saveSettings();
                } else {
                    settings.setUseImperial(false);
                    saveSettings();
                }
            }
        });

        switchMinutes5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {
                    settings.setRefreshTime(5);
                    saveSettings();
                    setAllSwitches();
                } else {
                    //nie wiem
                }
            }
        });

        switchMinutes10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {
                    settings.setRefreshTime(10);
                    saveSettings();
                    setAllSwitches();
                } else {
                    //nie wiem
                }
            }
        });

        switchMinutes20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {
                    settings.setRefreshTime(20);
                    saveSettings();
                    setAllSwitches();
                } else {
                    //nie wiem
                }
            }
        });

        switchMinutes30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {
                    settings.setRefreshTime(30);
                    saveSettings();
                    setAllSwitches();
                } else {
                    //nie wiem
                }
            }
        });

        retrieveSettings();
        setAllSwitches();
    }

    @Override
    public void onResume(){
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        imageSearch.setBackgroundColor(0x00FFFFFF);
        imageFav.setBackgroundColor(0x00FFFFFF);
        imageMenu.setBackgroundColor(0x20FFFFFF);
        super.onResume();

        setAllSwitches();
    }

    public void retrieveSettings(){
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

    public void saveSettings(){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(settings, Settings.class);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("settings", json);
        prefsEditor.commit();
    }

    public void setAllSwitches(){
        if(settings.isUseImperial()){
            switchImperial.setChecked(true);
        }
        switch(settings.getRefreshTime()){
            case 10:
                switchMinutes5.setChecked(false);
                switchMinutes10.setChecked(true);
                switchMinutes20.setChecked(false);
                switchMinutes30.setChecked(false);
                break;
            case 20:
                switchMinutes5.setChecked(false);
                switchMinutes10.setChecked(false);
                switchMinutes20.setChecked(true);
                switchMinutes30.setChecked(false);
                break;
            case 30:
                switchMinutes5.setChecked(false);
                switchMinutes10.setChecked(false);
                switchMinutes20.setChecked(false);
                switchMinutes30.setChecked(true);
                break;
            default:
                switchMinutes5.setChecked(true);
                switchMinutes10.setChecked(false);
                switchMinutes20.setChecked(false);
                switchMinutes30.setChecked(false);
        }
    }
}
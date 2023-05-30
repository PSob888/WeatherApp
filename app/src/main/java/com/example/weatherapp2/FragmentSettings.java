package com.example.weatherapp2;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentSettings extends Fragment {

    private FragmentActivity mainActivity;
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

        Spinner spinnerTime =view.findViewById(R.id.spinnerTime);

        initSpinner(spinnerTime);
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
    }

    private void initSpinner(Spinner spinnerTime) {
        String[] items = new String[]{
                "5 minutes", "10 minutes", "30 minutes", "1 hour",
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        spinnerTime.setAdapter(adapter);
    }
}
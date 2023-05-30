package com.example.weatherapp2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class FragmentFavourite extends Fragment {

    private FragmentActivity mainActivity;

    public FragmentFavourite() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onResume(){
        EditText editText = mainActivity.findViewById(R.id.editTextTextPersonName);
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);

        editText.setBackgroundColor(0x00FFFFFF);
        imageSearch.setBackgroundColor(0x00FFFFFF);
        imageFav.setBackgroundColor(0x20FFFFFF);
        imageMenu.setBackgroundColor(0x00FFFFFF);
        super.onResume();
    }
}
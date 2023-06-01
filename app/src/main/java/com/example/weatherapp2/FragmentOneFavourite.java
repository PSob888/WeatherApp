package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOneFavourite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOneFavourite extends Fragment {

    private MainActivity mainActivity;
    TextView textCity;
    TextView textTemp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentOneFavourite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOneFavourite.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOneFavourite newInstance(String param1, String param2) {
        FragmentOneFavourite fragment = new FragmentOneFavourite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_favourite, container, false);

        // Access and modify the views inside the fragment

        // Return the inflated view
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        textCity = view.findViewById(R.id.textCityNameOneFav);
        textTemp = view.findViewById(R.id.textTemperatureOneFav);
        textCity.setText(mParam1);
        textTemp.setText(mParam2);
    }

    public void onResume(){
        super.onResume();
        updateValues();
    }

    public void updateValues(){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(mParam1, "");
        WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);

        json = mPrefs.getString("settings", "");
        Settings settings = gson.fromJson(json, Settings.class);

//        Double temp;
//        String tempe = "";
//        if(!settings.isUseImperial()){
//            temp = round(weatherPanel.getWeatherResponse().getMain().getTemp()-273.15, 1);
//            tempe += temp.toString();
//            tempe += " *C";
//        }
//        else{
//            temp = round(((weatherPanel.getWeatherResponse().getMain().getTemp()-273.15) * 9/5) + 32, 1);
//            tempe += temp.toString();
//            tempe += " *F";
//        }
//        textTemp.setText(tempe);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
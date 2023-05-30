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
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentWeather extends Fragment {

    private MainActivity mainActivity;
    String url = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    String api_key = "72f43f52d4476175f100769b6a0ab5cd";
    public FragmentWeather() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onResume(){
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        imageSearch.setBackgroundColor(0x20FFFFFF);
        imageFav.setBackgroundColor(0x00FFFFFF);
        imageMenu.setBackgroundColor(0x00FFFFFF);
        super.onResume();
    }

    public void getWeather(View v, String cityName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi myapi = retrofit.create(Weatherapi.class);

        Call<Test> testCall = myapi.getWeather(cityName, api_key);
        testCall.enqueue(new Callback<Test>() {
            @Override
            public void onResponse(Call<Test> call, Response<Test> response) {
                if(response.code() == 404){
                    Toast.makeText(getActivity() , "Please enter a valid city", Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful())){
                    Toast.makeText(getActivity() , response.code(), Toast.LENGTH_LONG).show();
                }
                Test myData = response.body();
                Weather weather = myData.getWeather();
            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {

            }
        });
    }
}
package com.example.weatherapp2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentWeather extends Fragment {

    private MainActivity mainActivity;
    String url = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    String api_key = "72f43f52d4476175f100769b6a0ab5cd";
    WeatherResponse current;
    TextView textCityName;
    TextView textHour;
    TextView textLastUpdated;
    TextView textTemperature;
    TextView textClouds;
    TextView textMoist;
    TextView textWind;
    TextView textPressure;
    EditText editText;
    ImageButton searchButton;
    ImageView cloudImage;
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
        textCityName = view.findViewById(R.id.textCityName);
        textHour = view.findViewById(R.id.textHour);
        textLastUpdated = view.findViewById(R.id.textLastUpdated);
        textTemperature = view.findViewById(R.id.textTemperature2);
        textClouds = view.findViewById(R.id.textClouds2);
        textMoist = view.findViewById(R.id.textMoist2);
        textWind = view.findViewById(R.id.textWind2);
        textPressure = view.findViewById(R.id.textPressure2);
        editText = view.findViewById(R.id.editText);
        searchButton = view.findViewById(R.id.imageButtonSerach);
        cloudImage = view.findViewById(R.id.imageTypeOfClouds);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = String.valueOf(editText.getText());
                getWeather(view, cityName);
            }
        });
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
        Weatherapi myApi = retrofit.create(Weatherapi.class);

        Call<WeatherResponse> testCall = myApi.getWeather(cityName, api_key);
        testCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.code() == 404){
                    Toast.makeText(getActivity() , "Please enter a valid city", Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful())){
                    Toast.makeText(getActivity() , response.code(), Toast.LENGTH_LONG).show();
                }
                WeatherResponse myData = response.body();
                current = myData;

                WeatherPanel weatherPanel = new WeatherPanel(current, new Date());

                textCityName.setText(current.getName());

                //Date time=new Date((long)current.getDt()*1000);
                SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss dd.mm.yyyy");
                textHour.setText(ft.format(time));
                textLastUpdated.setText(ft.format(time));

                Double temp = round(current.getMain().getTemp()-273.15, 1);
                textTemperature.setText(temp.toString());
                textClouds.setText(current.getWeatherList().get(0).getDescription());
                textMoist.setText(current.getMain().getHumidity().toString() + " %");
                textWind.setText(current.getWind().getSpeed().toString() + " km/h");
                textPressure.setText(current.getMain().getPressure().toString() + " hPa");

                String image_url="https://openweathermap.org/img/w/" + current.getWeatherList().get(0).getIcon() + ".png";
                Picasso.get().load(image_url).into(cloudImage);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity() , t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("MyTag", t.getMessage());
            }
        });
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
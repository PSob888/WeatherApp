package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
    WeatherPanel weatherPanel;
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
    ImageButton favButton;
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
        setAllTheTexts("current");
    }

    @Override
    public void onResume(){
        super.onResume();
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        imageSearch.setBackgroundColor(0x20FFFFFF);
        imageFav.setBackgroundColor(0x00FFFFFF);
        imageMenu.setBackgroundColor(0x00FFFFFF);
        setAllTheTexts("current");
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
                Log.d("JebaneGowno", myData.getName());

                WeatherPanel test = new WeatherPanel(new WeatherResponse(myData), Calendar.getInstance().getTime());
                Log.d("JebaneGowno", test.getWeatherResponse().getName());

                setWeatherPanel(test);
                writeWeatherDataToFile("current");
                setAllTheTexts("current");
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity() , t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("MyTag", t.getMessage());
            }
        });
    }

    private void writeWeatherDataToFile(String filename){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(weatherPanel, WeatherPanel.class);
        Log.d("JsonWrite", weatherPanel.getWeatherResponse().getName());
        Log.d("JsonWrite", json);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(filename, json);
        prefsEditor.commit();
//        try{
//            ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(filename)));
//            outputStream.writeObject(weatherPanel);
//            outputStream.flush();
//            outputStream.close();
//        } catch (IOException e) {
//            Toast.makeText(getActivity() , "Can't create the file", Toast.LENGTH_LONG).show();
//        }
    }

    private void setAllTheTexts(String filename){
        SharedPreferences  mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(filename, "");
        WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);

        current = weatherPanel.getWeatherResponse();

        textCityName.setText(weatherPanel.getWeatherResponse().getName());

        Date time=new Date((current.getDt()*1000) + (weatherPanel.getWeatherResponse().getTimezone()*1000));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        SimpleDateFormat ft2 = new SimpleDateFormat("HH:mm");
        textHour.setText(ft2.format(time));
        textLastUpdated.setText(formatter.format(weatherPanel.getUpdateDate()));

        Double temp = round(weatherPanel.getWeatherResponse().getMain().getTemp()-273.15, 1);
        textTemperature.setText(temp.toString());
        textClouds.setText(weatherPanel.getWeatherResponse().getWeatherList().get(0).getDescription());
        textMoist.setText(weatherPanel.getWeatherResponse().getMain().getHumidity().toString() + " %");
        textWind.setText(weatherPanel.getWeatherResponse().getWind().getSpeed().toString() + " km/h");
        textPressure.setText(weatherPanel.getWeatherResponse().getMain().getPressure().toString() + " hPa");

        String image_url="https://openweathermap.org/img/w/" + weatherPanel.getWeatherResponse().getWeatherList().get(0).getIcon() + ".png";
        Picasso.get().load(image_url).into(cloudImage);

//        try{
//            ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(filename)));
//            WeatherPanel weatherPanel = (WeatherPanel)inputStream.readObject();
//            inputStream.close();
//        } catch (IOException | ClassNotFoundException e) {
//            Toast.makeText(getActivity() , "Can't find the file", Toast.LENGTH_LONG).show();
//        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void setWeatherPanel(WeatherPanel weatherPanel) {
        this.weatherPanel = weatherPanel;
    }
}
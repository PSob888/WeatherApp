package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentWeather extends Fragment {

    private MainActivity mainActivity;
    Settings settings;
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
    TextView textTomorrow;
    TextView textInTwo;
    TextView textInThree;
    TextView textInFour;
    TextView textInFive;
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
        //clearAllFavourites();
        getNewestSettings();
        textCityName = view.findViewById(R.id.textCityName);
        textHour = view.findViewById(R.id.textHour);
        textLastUpdated = view.findViewById(R.id.textLastUpdated);
        textTemperature = view.findViewById(R.id.textTemperature2);
        textClouds = view.findViewById(R.id.textClouds2);
        textMoist = view.findViewById(R.id.textMoist2);
        textWind = view.findViewById(R.id.textWind2);
        textPressure = view.findViewById(R.id.textPressure2);
        textTomorrow = view.findViewById(R.id.textTomorrow2);
        textInTwo = view.findViewById(R.id.textInTwo2);
        textInThree = view.findViewById(R.id.textInThree2);
        textInFour = view.findViewById(R.id.textInFour2);
        textInFive = view.findViewById(R.id.textInFive2);
        editText = view.findViewById(R.id.editText);
        searchButton = view.findViewById(R.id.imageButtonSerach);
        cloudImage = view.findViewById(R.id.imageTypeOfClouds);
        favButton = view.findViewById(R.id.imageButtonAddToFav);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = String.valueOf(editText.getText());
                if(!cityName.equals("")){
                    getWeather(view, cityName);
                    getForecast(view, cityName);
                }
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = (String) textCityName.getText();
                List<String> fav = mainActivity.getFavourites();
                if(fav.contains(cityName)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setMessage("Do you want to remove this city?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fav.remove(cityName);
                            mainActivity.setFavourites(fav);
                            mainActivity.saveFavourites();

                            setAllTheTexts("current");
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    fav.add(cityName);
                    writeWeatherDataToFile(cityName);
                }
                mainActivity.setFavourites(fav);
                mainActivity.saveFavourites();

                setAllTheTexts("current");
            }
        });
        setAllTheTexts("current");
        getWeatherDataFromFile("current");
    }

    @Override
    public void onResume(){
        super.onResume();
        getNewestSettings();
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        imageSearch.setBackgroundColor(0x20FFFFFF);
        imageFav.setBackgroundColor(0x00FFFFFF);
        imageMenu.setBackgroundColor(0x00FFFFFF);
        setAllTheTexts("current");
        getWeatherDataFromFile("current");
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
                else{
                    WeatherResponse myData = response.body();

                    if(myData.getCoord() == null){
                        Toast.makeText(getActivity() , "No data, probably not a valid city", Toast.LENGTH_LONG).show();
                    }
                    else{

                        WeatherPanel test = new WeatherPanel(new WeatherResponse(myData), Calendar.getInstance().getTime());

                        setWeatherPanel(test);
                        writeWeatherDataToFile("current");
                        setAllTheTexts("current");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity() , t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("MyTag", t.getMessage());
            }
        });

    }

    public void getForecast(View v, String cityName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi myApi = retrofit.create(Weatherapi.class);

        Call<WeatherData> testCall = myApi.getForecast(cityName, api_key);
        testCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful()) {
                    WeatherData weatherData = response.body();
                    List<WeatherItem> dataList = weatherData.getList();

                    List<Double> doubles = new ArrayList<>();
                    for (WeatherItem weatherItem : dataList) {
                        doubles.add(weatherItem.getMain().getTemp());
                    }

                    List<Double> temps = new ArrayList<>();
                    for(int i=0; i<5; i++){
                        Double temp = doubles.get((i*8)) + doubles.get((i*8) + 1)
                                + doubles.get((i*8) + 2) + doubles.get((i*8) + 3)
                                + doubles.get((i*8) + 4) + doubles.get((i*8) + 5)
                                + doubles.get((i*8) + 6) + doubles.get((i*8) + 7);
                        temp = temp / 8.0;
                        temp = round(temp, 1);
                        temps.add(i,temp);
                    }
                    setWeatherPanelsTemp(temps);
                    writeWeatherDataToFile("current");
                    setAllTheTexts("current");
                } else {
                    Toast.makeText(getActivity() , "No data, probably not a valid city", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Toast.makeText(getActivity() , t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("MyTag", t.getMessage());
            }
        });
    }

    private void writeWeatherDataToFile(String filename){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(weatherPanel, WeatherPanel.class);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(filename, json);
        prefsEditor.commit();
    }

    private void getWeatherDataFromFile(String filename){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(filename, "");
        WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);
        this.weatherPanel = weatherPanel;
    }

    private void setAllTheTexts(String filename){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(filename, "");
        WeatherPanel weatherPanel = gson.fromJson(json, WeatherPanel.class);

        String name = weatherPanel.getWeatherResponse().getName();
        if(mainActivity.getFavourites().contains(name)){
            favButton.setImageResource(R.drawable.outline_favorite_36);
        }
        else{
            favButton.setImageResource(R.drawable.outline_favorite_border_36);
        }

        current = weatherPanel.getWeatherResponse();

        textCityName.setText(name);

        Date time=new Date((current.getDt()*1000) + (weatherPanel.getWeatherResponse().getTimezone()*1000));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        SimpleDateFormat ft2 = new SimpleDateFormat("HH:mm");
        textHour.setText(ft2.format(time));
        textLastUpdated.setText(formatter.format(weatherPanel.getUpdateDate()));

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
        textTemperature.setText(tempe);
        tempe = "";
        textClouds.setText(weatherPanel.getWeatherResponse().getWeatherList().get(0).getDescription());
        textMoist.setText(weatherPanel.getWeatherResponse().getMain().getHumidity().toString() + " %");
        textWind.setText(weatherPanel.getWeatherResponse().getWind().getSpeed().toString() + " km/h");
        textPressure.setText(weatherPanel.getWeatherResponse().getMain().getPressure().toString() + " hPa");

        String image_url="https://openweathermap.org/img/w/" + weatherPanel.getWeatherResponse().getWeatherList().get(0).getIcon() + ".png";
        Picasso.get().load(image_url).into(cloudImage);

        if(weatherPanel.getTemps() != null && weatherPanel.getTemps().get(0) != null){
            if(!settings.isUseImperial()){
                temp = round(weatherPanel.getTemps().get(0)-273.15, 1);
                tempe += temp.toString();
                tempe += " *C";
            }
            else{
                temp = round(((weatherPanel.getTemps().get(0)-273.15) * 9/5) + 32, 1);
                tempe += temp.toString();
                tempe += " *F";
            }
            textTomorrow.setText(tempe);
            tempe = "";
            if(!settings.isUseImperial()){
                temp = round(weatherPanel.getTemps().get(1)-273.15, 1);
                tempe += temp.toString();
                tempe += " *C";
            }
            else{
                temp = round(((weatherPanel.getTemps().get(1)-273.15) * 9/5) + 32, 1);
                tempe += temp.toString();
                tempe += " *F";
            }
            textInTwo.setText(tempe);
            tempe = "";
            if(!settings.isUseImperial()){
                temp = round(weatherPanel.getTemps().get(2)-273.15, 1);
                tempe += temp.toString();
                tempe += " *C";
            }
            else{
                temp = round(((weatherPanel.getTemps().get(2)-273.15) * 9/5) + 32, 1);
                tempe += temp.toString();
                tempe += " *F";
            }
            textInThree.setText(tempe);
            tempe = "";
            if(!settings.isUseImperial()){
                temp = round(weatherPanel.getTemps().get(3)-273.15, 1);
                tempe += temp.toString();
                tempe += " *C";
            }
            else{
                temp = round(((weatherPanel.getTemps().get(3)-273.15) * 9/5) + 32, 1);
                tempe += temp.toString();
                tempe += " *F";
            }
            textInFour.setText(tempe);
            tempe = "";
            if(!settings.isUseImperial()){
                temp = round(weatherPanel.getTemps().get(4)-273.15, 1);
                tempe += temp.toString();
                tempe += " *C";
            }
            else{
                temp = round(((weatherPanel.getTemps().get(4)-273.15) * 9/5) + 32, 1);
                tempe += temp.toString();
                tempe += " *F";
            }
            textInFive.setText(tempe);
            tempe = "";
        }
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

    public void clearAllFavourites(){
        List<String> fav = mainActivity.getFavourites();
        fav.clear();
        mainActivity.setFavourites(fav);
        mainActivity.saveFavourites();
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void setWeatherPanelsTemp(List<Double> temp) {
        this.weatherPanel.setTemps(temp);
    }
    public void setWeatherPanel(WeatherPanel weatherPanel) {
        this.weatherPanel = weatherPanel;
    }
}
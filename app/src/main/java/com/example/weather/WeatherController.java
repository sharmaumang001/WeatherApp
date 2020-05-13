package com.example.weather;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.R;


public class WeatherController extends AppCompatActivity {


    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    final String APP_ID = "e72____PLEASE_REPLACE_ME_____13";

    final long MIN_TIME = 5000;

    final float MIN_DISTANCE = 1000;


    TextView tempView , locationView;
    Button changeCityButton;
    ImageView weatherView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        locationView = findViewById(R.id.locationTV);
        tempView =findViewById(R.id.tempTV);
        changeCityButton =findViewById(R.id.changeCityButton);
        weatherView = findViewById(R.id.weather_image);


    }






}

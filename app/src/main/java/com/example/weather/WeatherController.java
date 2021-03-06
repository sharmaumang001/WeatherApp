package com.example.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.weather.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {


    final int REQUEST_CODE = 123;

    final String APP_ID = "5815127febc1e06290ed32df674e2826";

    final long MIN_TIME = 1000;

    final float MIN_DISTANCE = 1;

    String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?";

    SharedPreferences SP;




    String location_provider = LocationManager.GPS_PROVIDER;
    TextView tempView, locationView;
    Button changeCityButton,test_button;
    ImageView weatherView;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        locationView = findViewById(R.id.locationTV);
        tempView = findViewById(R.id.tempTV);
        changeCityButton = findViewById(R.id.changeCityButton);
        weatherView = findViewById(R.id.weather_image);



        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this,City_Change.class);
                startActivity(myIntent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("weather", "onResume() called");

        SharedPreferences prefs = getSharedPreferences("ChatPrefs",MODE_PRIVATE);
        SharedPreferences prefs1 = getSharedPreferences("ChatPrefs1",MODE_PRIVATE);


        String longitude =prefs.getString("LONGITUDE",null);
        String latitude =prefs.getString("LATTITUDE",null);

        Log.d("weather","THE LONGITUDE IS "+longitude+"   THE LATITUDE IS "+latitude);

        RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", APP_ID);

        someNetworking(params);

        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("city");

        if (city != null) {
            getWeatherForNewcity(city);

        } else {

            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewcity(String city){

        RequestParams parmas_1= new RequestParams();
        parmas_1.put("q",city);
        parmas_1.put("appid",APP_ID);

        someNetworking(parmas_1);
    }

    private void getWeatherForCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("weather", "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);

                someNetworking(params);

                saveLocation(latitude,longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("weather", "onProviderEnables() callback received ");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("weather", "onProviderDisables() callback received ");

            }};


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(location_provider,MIN_TIME,MIN_DISTANCE,locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Log.d("weather", "onRequestPermissionResult() :permission Granted");
                getWeatherForCurrentLocation();
            }else
                Log.d("weather","onRequestPermissionResult() :permission Denied");
        }
    }

    private void someNetworking(RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL,params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("weather","DATA COLLECTED"+ response.toString() );
                Toast.makeText(WeatherController.this,"Location FOUND",Toast.LENGTH_SHORT).show();

                WeatherDataModel weatherData = WeatherDataModel.fromJSON(response);
                Update_UI(weatherData);
            }

            @Override
            public  void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Log.e("weather","Fail  "+e.toString());
                Log.d("weather","Fail..status code"+statusCode);
             }
        });
    }

    private void Update_UI(WeatherDataModel Weather) {

        tempView.setText(Weather.getTempreture());
        locationView.setText(Weather.getCityName());
        int resourceID = getResources().getIdentifier(Weather.getIconName(),"drawable",getPackageName());
        weatherView.setImageResource(resourceID);
    }


    public void saveLocation(String latitude, String longitude){
        SharedPreferences prefs = getSharedPreferences("ChatPrefs",0);
        prefs.edit().putString("LATITUDE",latitude).apply();

        SharedPreferences prefs2 = getSharedPreferences("ChatPrefs1",0);
        prefs2.edit().putString("LONGITUDE",longitude).apply();
    };

}


package com.example.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    final String APP_ID = "1546c83c2a8959e4a96fa461eaee6eea";

    final long MIN_TIME = 5000;

    final float MIN_DISTANCE = 1000;





    String location_provider = LocationManager.NETWORK_PROVIDER;
    TextView tempView, locationView;
    Button changeCityButton;
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


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("weather", "onResume() called");
        getWeatherForCurrentLocation();

    }

    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("weather", "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latittude = String.valueOf(location.getLatitude());

                RequestParams params = new RequestParams();
                params.put("lat",latittude);
                params.put("long",longitude);
                params.put("appID",APP_ID);
                someNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("weather", "onProviderDisables() callback received ");

            }
        };

        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(location_provider, MIN_TIME, MIN_DISTANCE, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Log.d("weather", "onRequestPermissionResult() :permission Granted");
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

                WeatherDataModel weatherData = WeatherDataModel.fromJSON(response);

            }

            @Override
            public  void onFailure(int statusCode, Header[] headers,Throwable e, JSONObject response){
                Toast.makeText(WeatherController.this,"Request Failed",Toast.LENGTH_SHORT).show();
            }

        });








    }



}

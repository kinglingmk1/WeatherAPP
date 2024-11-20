package com.example.ca_assignment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
public class FiveDayRepo extends AppCompatActivity {
    boolean lang;
    Button todayWeather, fiveDayRepo, fetchButton;
    TextView title, place, onOffline,day1WeekN, day1Date, day1Temp,day1Description,day2WeekN, day2Date, day2Temp, day2Description,day3WeekN, day3Date, day3Temp, day3Description,day4WeekN, day4Date, day4Temp, day4Description,day5WeekN, day5Date, day5Temp, day5Description;
    WebView webImage1, webImage2,webImage3,webImage4,webImage5;
    LocationManager mLocationManager;
    Calendar calendar;
    String apiKey, apiURL, posLat, posLon, api,apiC,iconAPI,fetch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day_repo);
        title = findViewById(R.id.AppTitle);
        place = findViewById(R.id.placeData);
        onOffline = findViewById(R.id.fOnOffline);
        day1WeekN = findViewById(R.id.weekRow1);
        day1Date = findViewById(R.id.dayRow1);
        day1Temp = findViewById(R.id.status1);
        day1Description = findViewById(R.id.weatherDescription1);
        day2WeekN = findViewById(R.id.weekRow2);
        day2Date = findViewById(R.id.dayRow2);
        day2Temp = findViewById(R.id.status2);
        day2Description = findViewById(R.id.weatherDescription2);
        day3WeekN = findViewById(R.id.weekRow3);
        day3Date = findViewById(R.id.dayRow3);
        day3Temp = findViewById(R.id.status3);
        day3Description = findViewById(R.id.weatherDescription3);
        day4WeekN = findViewById(R.id.weekRow4);
        day4Date = findViewById(R.id.dayRow4);
        day4Temp = findViewById(R.id.status4);
        day4Description = findViewById(R.id.weatherDescription4);
        day5WeekN = findViewById(R.id.weekRow5);
        day5Date = findViewById(R.id.dayRow5);
        day5Temp = findViewById(R.id.status5);
        day5Description = findViewById(R.id.weatherDescription5);
        webImage1 = findViewById(R.id.weatherImage1);
        webImage2 = findViewById(R.id.weatherImage2);
        webImage3 = findViewById(R.id.weatherImage3);
        webImage4 = findViewById(R.id.weatherImage4);
        webImage5 = findViewById(R.id.weatherImage5);
        todayWeather = findViewById(R.id.weatherButton);
        fiveDayRepo = findViewById(R.id.fiveDayRepo);
        fetchButton = findViewById(R.id.fetchDataButton);
        calendar = Calendar.getInstance();
        fiveDayRepo.setTextColor(Color.parseColor("#FFFFFF"));
        Intent intent = getIntent();
        if(intent.getStringExtra("lang")!=null){
            String sLang = intent.getStringExtra("lang");
            lang = !sLang.equals("false");
            fetch = (lang) ? "五天天氣預報" : "Five Days Report";
            title.setText(fetch);
            fetch = (lang) ? "同步中..." : "Fetching...";
            place.setText(fetch);
            fetchButton.setText(lang ? "資料同步" : "Fetch Data");
            todayWeather.setText(lang ? "今日天氣" : "Today's Weather");
            fiveDayRepo.setText(lang ? "五天預報" : "5 Days Report");
        }
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdates();
        todayWeather.setOnClickListener(view -> {
            Intent intent1 = new Intent(FiveDayRepo.this, MainActivity.class);
            intent1.putExtra("lang", String.valueOf(lang));
            startActivity(intent1);
        });
        fetchButton.setOnClickListener(view -> fetchData());
        dayNightTheme(calendar);
        weekSet();
    }
    private final LocationListener mLocationListener;
    {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                apiKey = "&units=metric&apikey=d3623938c0490357b3dd1029b77bd8a0";
                apiURL = "https://api.openweathermap.org/data/2.5/forecast";
                posLat = "?lat=" + location.getLatitude();
                posLon = "&lon=" + location.getLongitude();
                apiC = apiURL + "?lat=" + posLat + "&lon=" + posLon + "&lang=zh_tw" + apiKey;
                api = apiURL + "?lat=" + posLat + "&lon=" + posLon + apiKey;
                fetchData();
            }
        };
    }
    public void weekSet(){
        day1Date.setText(getTomorrow(1).substring(5));
        day2Date.setText(getTomorrow(2).substring(5));
        day3Date.setText(getTomorrow(3).substring(5));
        day4Date.setText(getTomorrow(4).substring(5));
        day5Date.setText(getTomorrow(5).substring(5));
        day1WeekN.setText(getWeekName(calendar.get(Calendar.DAY_OF_WEEK)));
        day2WeekN.setText(getWeekName(calendar.get(Calendar.DAY_OF_WEEK) + 1));
        day3WeekN.setText(getWeekName(calendar.get(Calendar.DAY_OF_WEEK) + 2));
        day4WeekN.setText(getWeekName(calendar.get(Calendar.DAY_OF_WEEK) + 3));
        day5WeekN.setText(getWeekName(calendar.get(Calendar.DAY_OF_WEEK) + 4));
    }
    @SuppressLint("SimpleDateFormat")
    public static String getTomorrow(int i) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, i);
        return new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
    }
    public String getWeekName(int weekNum) {
        String[] weekdays = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        String[] CWeekdays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        if(weekNum>7){
            weekNum = weekNum - 7;
        }
        if (weekNum >= 1 && weekNum <= 7 && lang) {
            return CWeekdays[weekNum - 1];
        } else if (weekNum >= 1 && weekNum <= 7 && !lang){
            return weekdays[weekNum - 1];
        }
        return null;
    }
    public void dayNightTheme(Calendar calendar){
        if(calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<= 17 ){
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#72CEE4"));
            webImage1.setBackgroundColor(Color.parseColor("#72CEE4"));
            webImage2.setBackgroundColor(Color.parseColor("#72CEE4"));
            webImage3.setBackgroundColor(Color.parseColor("#72CEE4"));
            webImage4.setBackgroundColor(Color.parseColor("#72CEE4"));
            webImage5.setBackgroundColor(Color.parseColor("#72CEE4"));
            onOffline.setTextColor(Color.parseColor("#000000"));
            title.setTextColor(Color.parseColor("#000000"));
            place.setTextColor(Color.parseColor("#000000"));
            day1WeekN.setTextColor(Color.parseColor("#000000"));
            day1Date.setTextColor(Color.parseColor("#000000"));
            day1Temp.setTextColor(Color.parseColor("#000000"));
            day1Description.setTextColor(Color.parseColor("#000000"));
            day2WeekN.setTextColor(Color.parseColor("#000000"));
            day2Date.setTextColor(Color.parseColor("#000000"));
            day2Temp.setTextColor(Color.parseColor("#000000"));
            day2Description.setTextColor(Color.parseColor("#000000"));
            day3WeekN.setTextColor(Color.parseColor("#000000"));
            day3Date.setTextColor(Color.parseColor("#000000"));
            day3Temp.setTextColor(Color.parseColor("#000000"));
            day3Description.setTextColor(Color.parseColor("#000000"));
            day4WeekN.setTextColor(Color.parseColor("#000000"));
            day4Date.setTextColor(Color.parseColor("#000000"));
            day4Temp.setTextColor(Color.parseColor("#000000"));
            day4Description.setTextColor(Color.parseColor("#000000"));
            day5WeekN.setTextColor(Color.parseColor("#000000"));
            day5Date.setTextColor(Color.parseColor("#000000"));
            day5Temp.setTextColor(Color.parseColor("#000000"));
            day5Description.setTextColor(Color.parseColor("#000000"));
        }else {
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#343336"));
            webImage1.setBackgroundColor(Color.parseColor("#343336"));
            webImage2.setBackgroundColor(Color.parseColor("#343336"));
            webImage3.setBackgroundColor(Color.parseColor("#343336"));
            webImage4.setBackgroundColor(Color.parseColor("#343336"));
            webImage5.setBackgroundColor(Color.parseColor("#343336"));
            onOffline.setTextColor(Color.parseColor("#FFFFFF"));
            title.setTextColor(Color.parseColor("#FFFFFF"));
            place.setTextColor(Color.parseColor("#FFFFFF"));
            day1WeekN.setTextColor(Color.parseColor("#FFFFFF"));
            day1Date.setTextColor(Color.parseColor("#FFFFFF"));
            day1Temp.setTextColor(Color.parseColor("#FFFFFF"));
            day1Description.setTextColor(Color.parseColor("#FFFFFF"));
            day2WeekN.setTextColor(Color.parseColor("#FFFFFF"));
            day2Date.setTextColor(Color.parseColor("#FFFFFF"));
            day2Temp.setTextColor(Color.parseColor("#FFFFFF"));
            day2Description.setTextColor(Color.parseColor("#FFFFFF"));
            day3WeekN.setTextColor(Color.parseColor("#FFFFFF"));
            day3Date.setTextColor(Color.parseColor("#FFFFFF"));
            day3Temp.setTextColor(Color.parseColor("#FFFFFF"));
            day3Description.setTextColor(Color.parseColor("#FFFFFF"));
            day4WeekN.setTextColor(Color.parseColor("#FFFFFF"));
            day4Date.setTextColor(Color.parseColor("#FFFFFF"));
            day4Temp.setTextColor(Color.parseColor("#FFFFFF"));
            day4Description.setTextColor(Color.parseColor("#FFFFFF"));
            day5WeekN.setTextColor(Color.parseColor("#FFFFFF"));
            day5Date.setTextColor(Color.parseColor("#FFFFFF"));
            day5Temp.setTextColor(Color.parseColor("#FFFFFF"));
            day5Description.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
    private static final int REQUEST_LOCATION = 128;
    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5f, mLocationListener);
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                onOffline.setText("Getting Location...");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void fetchData() {
        dayNightTheme(calendar);
        place.setText(lang ? "同步中..." : "Fetching...");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            double latitude = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
            double longitude = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            apiC = apiURL + "?lat=" + latitude + "&lon=" + longitude + "&lang=zh_tw" + apiKey;
            api = apiURL + "?lat=" + latitude + "&lon=" + longitude + apiKey;
            new Thread(() -> {
                try {
                    final JSONObject weatherData = fetchWeatherData(api);
                    final JSONObject weatherDataC = fetchWeatherDataC(apiC);
                    runOnUiThread(() -> {
                        if (weatherData != null && !lang) {
                            try {
                                weekSet();
                                saveJsonToFile(weatherData);
                                saveJsonToFileC(weatherDataC);
                                String cityName = weatherData.getJSONObject("city").getString("name");
                                place.setText(cityName);
                                day1Temp.setText("Temp: "+(int)weatherData.getJSONArray("list").getJSONObject(7).getJSONObject("main").getDouble("temp")+"°C");
                                day2Temp.setText("Temp: "+(int)weatherData.getJSONArray("list").getJSONObject(15).getJSONObject("main").getDouble("temp")+"°C");
                                day3Temp.setText("Temp: "+(int)weatherData.getJSONArray("list").getJSONObject(23).getJSONObject("main").getDouble("temp")+"°C");
                                day4Temp.setText("Temp: "+(int)weatherData.getJSONArray("list").getJSONObject(31).getJSONObject("main").getDouble("temp")+"°C");
                                day5Temp.setText("Temp: "+(int)weatherData.getJSONArray("list").getJSONObject(39).getJSONObject("main").getDouble("temp")+"°C");
                                day1Description.setText(weatherData.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + weatherData.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                                day2Description.setText(weatherData.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + weatherData.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                                day3Description.setText(weatherData.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + weatherData.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                                day4Description.setText(weatherData.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + weatherData.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                                day5Description.setText(weatherData.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + weatherData.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                                //webImage1.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                                if(calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<= 17 ){
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherData.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage1.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage1.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherData.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage2.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage2.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherData.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage3.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage3.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherData.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage4.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage4.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherData.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage5.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage5.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                } else {
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherData.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage1.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage1.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherData.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage2.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage2.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherData.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage3.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage3.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherData.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage4.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage4.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherData.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage5.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage5.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                }
                                webImage1.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage2.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage3.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage4.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage5.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                onOffline.setText(lang ? "已連接" : "Connected");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if(weatherDataC!= null && lang){
                            try {
                                weekSet();
                                saveJsonToFile(weatherData);
                                saveJsonToFileC(weatherDataC);
                                String cityName = weatherDataC.getJSONObject("city").getString("name");
                                place.setText(cityName);
                                day1Temp.setText("溫度: "+(int)weatherDataC.getJSONArray("list").getJSONObject(7).getJSONObject("main").getDouble("temp")+"°C");
                                day2Temp.setText("溫度: "+(int)weatherDataC.getJSONArray("list").getJSONObject(15).getJSONObject("main").getDouble("temp")+"°C");
                                day3Temp.setText("溫度: "+(int)weatherDataC.getJSONArray("list").getJSONObject(23).getJSONObject("main").getDouble("temp")+"°C");
                                day4Temp.setText("溫度: "+(int)weatherDataC.getJSONArray("list").getJSONObject(31).getJSONObject("main").getDouble("temp")+"°C");
                                day5Temp.setText("溫度: "+(int)weatherDataC.getJSONArray("list").getJSONObject(39).getJSONObject("main").getDouble("temp")+"°C");
                                day1Description.setText(weatherDataC.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("description"));
                                day2Description.setText(weatherDataC.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("description"));
                                day3Description.setText(weatherDataC.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("description"));
                                day4Description.setText(weatherDataC.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("description"));
                                day5Description.setText(weatherDataC.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("description"));
                                if(calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<= 17 ){
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherDataC.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage1.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage1.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherDataC.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage2.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage2.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherDataC.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage3.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage3.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherDataC.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage4.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage4.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/"+ weatherDataC.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage5.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webImage5.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                } else {
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherDataC.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage1.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage1.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherDataC.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage2.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage2.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherDataC.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage3.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage3.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherDataC.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage4.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage4.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                    iconAPI = "https://openweathermap.org/img/wn/" + weatherDataC.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                    webImage5.setBackgroundColor(Color.parseColor("#343336"));
                                    webImage5.loadDataWithBaseURL(iconAPI, "<center><img src=" + iconAPI + "></img></center>", "text/html", "UTF-8", null);
                                }
                                webImage1.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage2.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage3.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage4.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                webImage5.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                onOffline.setText(lang ? "已連接" : "Connected");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } else {
            runOnUiThread(() -> {
                onOffline.setText(lang ? "載入離綫資料..." :"Loading offline data...");
                if(new File(getFilesDir(), "five_day_weather.json").exists() || new File(getFilesDir(), "five_day_weatherC.json").exists()){
                    //Load offline data
                    JSONObject loadedData = loadJsonFromFile();
                    JSONObject loadedData2 = loadJsonFromFileC();
                    if (loadedData != null && !lang) {
                        try {
                            weekSet();
                            place.setText(loadedData.getJSONObject("city").getString("name"));
                            day1Temp.setText("Temp: "+(int)loadedData.getJSONArray("list").getJSONObject(7).getJSONObject("main").getDouble("temp")+"°C");
                            day2Temp.setText("Temp: "+(int)loadedData.getJSONArray("list").getJSONObject(15).getJSONObject("main").getDouble("temp")+"°C");
                            day3Temp.setText("Temp: "+(int)loadedData.getJSONArray("list").getJSONObject(23).getJSONObject("main").getDouble("temp")+"°C");
                            day4Temp.setText("Temp: "+(int)loadedData.getJSONArray("list").getJSONObject(31).getJSONObject("main").getDouble("temp")+"°C");
                            day5Temp.setText("Temp: "+(int)loadedData.getJSONArray("list").getJSONObject(39).getJSONObject("main").getDouble("temp")+"°C");
                            day1Description.setText(loadedData.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + loadedData.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                            day2Description.setText(loadedData.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + loadedData.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                            day3Description.setText(loadedData.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + loadedData.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                            day4Description.setText(loadedData.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + loadedData.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                            day5Description.setText(loadedData.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase() + loadedData.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (loadedData2 != null && lang){
                        try {
                            weekSet();
                            place.setText(loadedData2.getJSONObject("city").getString("name"));
                            day1Temp.setText("溫度: "+(int)loadedData2.getJSONArray("list").getJSONObject(7).getJSONObject("main").getDouble("temp")+"°C");
                            day2Temp.setText("溫度: "+(int)loadedData2.getJSONArray("list").getJSONObject(15).getJSONObject("main").getDouble("temp")+"°C");
                            day3Temp.setText("溫度: "+(int)loadedData2.getJSONArray("list").getJSONObject(23).getJSONObject("main").getDouble("temp")+"°C");
                            day4Temp.setText("溫度: "+(int)loadedData2.getJSONArray("list").getJSONObject(31).getJSONObject("main").getDouble("temp")+"°C");
                            day5Temp.setText("溫度: "+(int)loadedData2.getJSONArray("list").getJSONObject(39).getJSONObject("main").getDouble("temp")+"°C");
                            day1Description.setText(loadedData2.getJSONArray("list").getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("description"));
                            day2Description.setText(loadedData2.getJSONArray("list").getJSONObject(15).getJSONArray("weather").getJSONObject(0).getString("description"));
                            day3Description.setText(loadedData2.getJSONArray("list").getJSONObject(23).getJSONArray("weather").getJSONObject(0).getString("description"));
                            day4Description.setText(loadedData2.getJSONArray("list").getJSONObject(31).getJSONArray("weather").getJSONObject(0).getString("description"));
                            day5Description.setText(loadedData2.getJSONArray("list").getJSONObject(39).getJSONArray("weather").getJSONObject(0).getString("description"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(FiveDayRepo.this, lang ?"離綫":"Offline", Toast.LENGTH_SHORT).show();
                    onOffline.setText(lang ? "離綫資料使用中": "Using offline data");
                } else {
                    //No data, pls connect internet to fetch
                    onOffline.setText(lang ? "沒有離綫資料 請連接互聯網同步" : "No offline data connect internet to fetch");
                    place.setText(lang ?"沒有資料":"No Data");
                    day1Temp.setText(lang ?"沒有資料":"No Data");
                    day1Description.setText(lang ?"沒有資料":"No Data");
                    day2Temp.setText(lang ?"沒有資料":"No Data");
                    day2Description.setText(lang ?"沒有資料":"No Data");
                    day3Temp.setText(lang ?"沒有資料":"No Data");
                    day3Description.setText(lang ?"沒有資料":"No Data");
                    day4Temp.setText(lang ?"沒有資料":"No Data");
                    day4Description.setText(lang ?"沒有資料":"No Data");
                    day5Temp.setText(lang ?"沒有資料":"No Data");
                    day5Description.setText(lang ?"沒有資料":"No Data");
                    Toast.makeText(FiveDayRepo.this, lang ? "請連接互聯網同步" : "Connect to the internet to fetch", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void saveJsonToFile(JSONObject jsonData) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("five_day_weather.json", Context.MODE_PRIVATE);
            String jsonString = jsonData.toString();
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveJsonToFileC(JSONObject jsonData) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("five_day_weatherC.json", Context.MODE_PRIVATE);
            String jsonString = jsonData.toString();
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private JSONObject loadJsonFromFile() {
        JSONObject jsonData = null;
        try {
            FileInputStream fileInputStream = openFileInput("five_day_weather.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            fileInputStream.close();
            jsonData = new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
    private JSONObject loadJsonFromFileC() {
        JSONObject jsonData = null;
        try {
            FileInputStream fileInputStream = openFileInput("five_day_weatherC.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            fileInputStream.close();
            jsonData = new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
    public static JSONObject fetchWeatherData(String apiURL) throws IOException, JSONException {
        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int FETCH_TIMEOUT = 10000;
        connection.setConnectTimeout(FETCH_TIMEOUT);
        connection.setReadTimeout(FETCH_TIMEOUT);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();
            connection.disconnect();
            return new JSONObject(response.toString());
        }
        return null;
    }
    public static JSONObject fetchWeatherDataC(String apiURL) throws IOException, JSONException {
        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int FETCH_TIMEOUT = 10000;
        connection.setConnectTimeout(FETCH_TIMEOUT);
        connection.setReadTimeout(FETCH_TIMEOUT);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();
            connection.disconnect();
            return new JSONObject(response.toString());
        }
        return null;
    }
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,1,1,"中文");
        menu.add(0,2,2,"English");
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("SetTextI18n")
    public boolean onOptionsItemSelected(MenuItem item) {
        lang = item.getItemId()==1;
        title.setText((item.getItemId()==1)? "五天天氣預報" : "Five Days Report");
        fetchButton.setText((item.getItemId()==1)? "資料同步" : "Fetch Data");
        todayWeather.setText((item.getItemId()==1)? "今日天氣" : "Today's Weather");
        fiveDayRepo.setText((item.getItemId()==1)? "五天預報" : "5 Days Report");
        fetchData();
        return true;
    }
    @Override
    public void onBackPressed(){
        String exit,Message,y,n;
        exit = lang ? "退出" : "Exit";
        Message = lang ? "你確定你真的要退出？" : "Are you sure you want to exit?";
        y = lang ? "確認" : "Yes";
        n = lang ? "返回" : "No";
        Dialog dialog = new AlertDialog.Builder(FiveDayRepo.this).setTitle(exit).setMessage(Message).setPositiveButton(y, (dialogInterface, i) -> System.exit(0)).setNegativeButton(n, null).create();
        dialog.show();
    }
}
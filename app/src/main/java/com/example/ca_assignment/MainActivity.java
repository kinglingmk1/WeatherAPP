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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String apiKey, apiURL, positionData, posLat, posLon, api,apiC,iconAPI,fetch;
    TextView weekName, currentTemp, currentPosition,currentWeather,onOffline,appTitle,todayTitle;
    Calendar calendar;
    LocationManager mLocationManager;
    Button fetchButton,weatherButton,fiveDayButton;
    WebView webView;
    boolean lang;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lang = false;
        setContentView(R.layout.activity_main);
        weekName = findViewById(R.id.weekName);
        currentTemp = findViewById(R.id.placeData);
        currentPosition = findViewById(R.id.currentPosition);
        currentWeather = findViewById(R.id.todayWeathers);
        onOffline = findViewById(R.id.onOffline);
        fetchButton = findViewById(R.id.fetchDataButton);
        weatherButton = findViewById(R.id.weatherButton);
        fiveDayButton = findViewById(R.id.fiveDayRepo);
        webView = findViewById(R.id.weatherImage);
        appTitle = findViewById(R.id.AppTitle);
        todayTitle = findViewById(R.id.todayTitle);
        weatherButton.setTextColor(Color.parseColor("#FFFFFF"));
        Intent intent = getIntent();
        if(intent.getStringExtra("lang")!=null){
            String sLang = intent.getStringExtra("lang");
            lang = !sLang.equals("false");
            fetch = (lang) ? "同步中..." : "Fetching...";
            currentPosition.setText(fetch);
            currentTemp.setText(fetch);
            onOffline.setText(fetch);
            weekName.setText(fetch);
            currentWeather.setText(fetch);
            fetchButton.setText(lang ?"資料同步" : "Fetch Data");
            weatherButton.setText(lang ? "今日天氣" : "Today's Weather");
            fiveDayButton.setText(lang ? "五天預報" : "5 Days Report");
            appTitle.setText(lang ? "天氣預報" : "Weather App");
            todayTitle.setText(lang ? "今日氣溫" : "Today's Tempture");
        }
        calendar = Calendar.getInstance();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdates();
        fetchButton.setOnClickListener(view -> fetchData());
        fiveDayButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(MainActivity.this, FiveDayRepo.class);
            intent1.putExtra("lang", String.valueOf(lang));
            startActivity(intent1);
        });
        dayNightTheme(calendar);
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            positionData = "Latitude: " + location.getLatitude() + System.lineSeparator() + "Longitude: " + location.getLongitude();
            apiKey = "&units=metric&apikey=d3623938c0490357b3dd1029b77bd8a0";
            apiURL = "https://api.openweathermap.org/data/2.5/weather/";
            posLat = "?lat=" + location.getLatitude();
            posLon = "&lon=" + location.getLongitude();
            api = apiURL + posLat + posLon + apiKey;
            fetchData();
        }
        @SuppressLint("SetTextI18n")
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            currentPosition.setText("Getting Location...");
        }
        @SuppressLint("SetTextI18n")
        public void onProviderDisabled(@NonNull String provider) {
            currentPosition.setText("GPS is turn off...");
        }
    };
    public String getWeekName(int weekNum) {
        String[] weekdays = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        String[] CWeekdays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        if (weekNum >= 1 && weekNum <= 7 && lang) {
            return CWeekdays[weekNum - 1];
        } else if (weekNum >= 1 && weekNum <= 7 && !lang){
            return weekdays[weekNum - 1];
        } else {
            return null;
        }
    }
    public void dayNightTheme(Calendar calendar){
        if(calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<= 17 ){
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#72CEE4"));
            webView.setBackgroundColor(Color.parseColor("#72CEE4"));
            weekName.setTextColor(Color.parseColor("#000000"));
            currentTemp.setTextColor(Color.parseColor("#000000"));
            currentPosition.setTextColor(Color.parseColor("#000000"));
            currentWeather.setTextColor(Color.parseColor("#000000"));
            onOffline.setTextColor(Color.parseColor("#000000"));
            appTitle.setTextColor(Color.parseColor("#000000"));
            todayTitle.setTextColor(Color.parseColor("#000000"));
        }else {
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#343336"));
            webView.setBackgroundColor(Color.parseColor("#343336"));
            weekName.setTextColor(Color.parseColor("#FFFFFF"));
            currentTemp.setTextColor(Color.parseColor("#FFFFFF"));
            currentPosition.setTextColor(Color.parseColor("#FFFFFF"));
            currentWeather.setTextColor(Color.parseColor("#FFFFFF"));
            onOffline.setTextColor(Color.parseColor("#FFFFFF"));
            appTitle.setTextColor(Color.parseColor("#FFFFFF"));
            todayTitle.setTextColor(Color.parseColor("#FFFFFF"));
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
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 500f, mLocationListener);
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                currentPosition.setText("Getting Location...");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void fetchData() {
        weekName.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + getWeekName(calendar.get(Calendar.DAY_OF_WEEK)));
        dayNightTheme(calendar);
        currentPosition.setText(fetch);
        currentTemp.setText(fetch);
        onOffline.setText(fetch);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
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
                    final JSONObject weatherDataC = fetchWeatherData(apiC);
                    runOnUiThread(() -> {
                        if (weatherData != null && !lang) {
                            try {
                                saveJsonToFile(weatherData);
                                saveJsonToFileC(weatherDataC);
                                currentPosition.setText(weatherData.getString("name"));
                                currentTemp.setText((int)weatherData.getJSONObject("main").getDouble("temp") + "°C");
                                currentWeather.setText(weatherData.getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase()+weatherData.getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                                iconAPI = "https://openweathermap.org/img/wn/"+ weatherData.getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                                if(calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<= 17 ){
                                    getWindow().getDecorView().setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webView.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webView.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                }else {
                                    getWindow().getDecorView().setBackgroundColor(Color.parseColor("#343336"));
                                    webView.setBackgroundColor(Color.parseColor("#343336"));
                                    webView.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    weekName.setTextColor(Color.parseColor("#FFFFFF"));
                                    currentTemp.setTextColor(Color.parseColor("#FFFFFF"));
                                    currentPosition.setTextColor(Color.parseColor("#FFFFFF"));
                                    currentWeather.setTextColor(Color.parseColor("#FFFFFF"));
                                    onOffline.setTextColor(Color.parseColor("#FFFFFF"));
                                    appTitle.setTextColor(Color.parseColor("#FFFFFF"));
                                    todayTitle.setTextColor(Color.parseColor("#FFFFFF"));
                                }
                                webView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                onOffline.setText(lang ? "已連接" : "Connected");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if(weatherDataC !=null && lang){
                            try {
                                saveJsonToFile(weatherData);
                                saveJsonToFileC(weatherDataC);
                                currentPosition.setText(weatherDataC.getString("name"));
                                currentTemp.setText((int)weatherDataC.getJSONObject("main").getDouble("temp") + "°C");
                                currentWeather.setText(weatherDataC.getJSONArray("weather").getJSONObject(0).getString("description"));
                                iconAPI = "https://openweathermap.org/img/wn/"+ weatherDataC.getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png";
                                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                                if(calendar.get(Calendar.HOUR_OF_DAY)>=6 && calendar.get(Calendar.HOUR_OF_DAY)<= 17 ){
                                    getWindow().getDecorView().setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webView.setBackgroundColor(Color.parseColor("#72CEE4"));
                                    webView.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    webView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                }else {
                                    getWindow().getDecorView().setBackgroundColor(Color.parseColor("#343336"));
                                    webView.setBackgroundColor(Color.parseColor("#343336"));
                                    webView.loadDataWithBaseURL(iconAPI, "<center><img src="+iconAPI+"></img></center>", "text/html", "UTF-8", null);
                                    webView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
                                    weekName.setTextColor(Color.parseColor("#FFFFFF"));
                                    currentTemp.setTextColor(Color.parseColor("#FFFFFF"));
                                    currentPosition.setTextColor(Color.parseColor("#FFFFFF"));
                                    currentWeather.setTextColor(Color.parseColor("#FFFFFF"));
                                    onOffline.setTextColor(Color.parseColor("#FFFFFF"));
                                    appTitle.setTextColor(Color.parseColor("#FFFFFF"));
                                    todayTitle.setTextColor(Color.parseColor("#FFFFFF"));
                                }
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
                onOffline.setText(lang ? "載入離綫資料..." : "Loading offline data...");
                currentPosition.setText(lang ? "載入中..." : "Loading...");
                currentTemp.setText(lang ? "載入中..." : "Loading...");
                currentWeather.setText(lang ? "載入中..." : "Loading...");
                if(new File(getFilesDir(), "current_weather.json").exists() || new File(getFilesDir(), "current_weatherC.json").exists()){
                    //Load offline data
                    JSONObject loadedData = loadJsonFromFile();
                    JSONObject loadedDataC = loadJsonFromFileC();
                    if (loadedData != null && !lang) {
                        try {
                            currentPosition.setText(loadedData.getString("name"));
                            currentTemp.setText((int)loadedData.getJSONObject("main").getDouble("temp") + "°C");
                            currentWeather.setText(loadedData.getJSONArray("weather").getJSONObject(0).getString("description").substring(0,1).toUpperCase()+loadedData.getJSONArray("weather").getJSONObject(0).getString("description").substring(1));
                            //webView.loadUrl(getFilesDir().toString()+"/icon.html"); //give up save webView in to storage and read it
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(loadedDataC != null && lang){
                        try {
                            currentPosition.setText(loadedDataC.getString("name"));
                            currentTemp.setText((int)loadedDataC.getJSONObject("main").getDouble("temp") + "°C");
                            currentWeather.setText(loadedDataC.getJSONArray("weather").getJSONObject(0).getString("description"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    onOffline.setText(lang ? "離綫資料使用中" : "Using offline data");
                    Toast.makeText(MainActivity.this, lang ? "離綫" : "Offline", Toast.LENGTH_SHORT).show();
                } else {
                    //No data, pls connect internet to fetch
                    onOffline.setText(lang ? "沒有離綫資料"+ System.lineSeparator()+"請連接互聯網同步" : "No offline data"+ System.lineSeparator()+"Connect internet to fetch");
                    currentPosition.setText(lang ? "沒有資料" : "No Data");
                    currentTemp.setText(lang ? "沒有資料" : "No Data");
                    currentWeather.setText(lang ? "沒有資料" : "No Data");
                    Toast.makeText(MainActivity.this, lang ? "請連接互聯網同步" : "Please connect to the internet to update", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void saveJsonToFile(JSONObject jsonData) {
        File path = getFilesDir();
        File file = new File(path, "current_weather.json");
        try(FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter)){
            writer.write(jsonData.toString());
        } catch (IOException e){
            Toast.makeText(MainActivity.this, "Fetch data save failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveJsonToFileC(JSONObject jsonData) {
        File path = getFilesDir();
        File file = new File(path, "current_weatherC.json");
        try(FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter)){
            writer.write(jsonData.toString());
        } catch (IOException e){
            Toast.makeText(MainActivity.this, "同步資料儲存失敗", Toast.LENGTH_SHORT).show();
        }
    }
    private JSONObject loadJsonFromFile() {
        JSONObject jsonData = null;
        try {
            FileInputStream fileInputStream = openFileInput("current_weather.json");
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
            FileInputStream fileInputStream = openFileInput("current_weatherC.json");
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
        } else {
            return null;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,1,1,"中文");
        menu.add(0,2,2,"English");
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("SetTextI18n")
    public boolean onOptionsItemSelected(MenuItem item) {
        lang = item.getItemId()==1;
        appTitle.setText((item.getItemId()==1)? "天氣預報" : "Weather App");
        todayTitle.setText((item.getItemId()==1)? "今日氣溫" : "Today's Temperature");
        fetchButton.setText((item.getItemId()==1)? "資料同步" : "Fetch Data");
        weatherButton.setText((item.getItemId()==1)? "今日天氣" : "Today's Weather");
        fiveDayButton.setText((item.getItemId()==1)? "五天預報" : "5 Days Report");
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
        Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(exit).setMessage(Message).setPositiveButton(y, (dialogInterface, i) -> System.exit(0)).setNegativeButton(n, null).create();
        dialog.show();
    }
}
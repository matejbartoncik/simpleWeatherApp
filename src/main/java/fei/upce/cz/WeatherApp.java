package fei.upce.cz;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WeatherApp {
    private String user_location;
    private double lat;
    private double lon;
    private String jsonResponse;
    private String weatherResponse;

    public WeatherApp(String user_location) {
        this.user_location = user_location;
        nameConvert();
    }

    private void parseJsonResponse() {
        JSONArray temp = new JSONArray(jsonResponse);
        lat = temp.getJSONObject(0).getDouble("lat");
        lon = temp.getJSONObject(0).getDouble("lon");
    }

    private void parseWeatherResponse() {
        SimpleDateFormat formatter = new SimpleDateFormat(" HH:mm:ss");
        Date date = new Date();
        JSONObject wrapObj = new JSONObject(weatherResponse);
        String description = wrapObj.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = wrapObj.getJSONObject("main").getDouble("temp");
        System.out.println(round(temperature,1) + "---" + description + "--" + formatter.format(date));
    }
    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private void nameConvert() {
        user_location = user_location.replace(" ", ".");
        user_location = user_location.toLowerCase();
    }


    public void APICallWeather() {
        final String APIKEY = "7fcce5dabf80a14dd91a0dc3ec527e3b";
        final String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + APIKEY + "&units=metric&lang=cz";
        final String newUrl = "https://api.openweathermap.org/data/3.0/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + APIKEY + "&units=metric&lang=cz";


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try {
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    weatherResponse = EntityUtils.toString(response.getEntity());
                    parseWeatherResponse();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void APICallCords() {
        final String APIKEY = "7fcce5dabf80a14dd91a0dc3ec527e3b";
        final String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + URLEncoder.encode(user_location, StandardCharsets.UTF_8) + "&limit=1&appid=" + APIKEY;


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try {
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    jsonResponse = EntityUtils.toString(response.getEntity());
                    parseJsonResponse();
                }
            } catch (IOException e) {
                System.out.println("Něco se pokazilo");
            } finally {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}






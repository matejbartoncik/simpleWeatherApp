package fei.upce.cz;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WeatherApp {
    private String user_location;

    public WeatherApp(String user_location) {
        this.user_location = user_location;
        nameConvert();
    }

    private void parseJsonResponse(String jsonResponse) {   //Ziská JSON data z API  které se volá ve funkci APICallCords a předává je funkci APICallWeather
        JSONArray temp = new JSONArray(jsonResponse);
        double lat = temp.getJSONObject(0).getDouble("lat");
        double lon = temp.getJSONObject(0).getDouble("lon");
        APICallWeather(lat, lon);
    }

    private void parseWeatherResponse(String weatherResponse, String user_location) { //Ziská JSON data z API  které se volá ve funkci APICallWeather a vypíše je na konzoli;
        SimpleDateFormat formatter = new SimpleDateFormat(" HH:mm:ss");
        String timestamp = formatter.format(new Date());
        JSONObject wrapObj = new JSONObject(weatherResponse);
        String description = wrapObj.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = round(wrapObj.getJSONObject("main").getDouble("temp"), 1);
        String output = "\n"+"Lokace: " + user_location + "\n" + "Teplota: " + temperature + "\n" + "Popis: " + description + "\n" + "Čas: " + timestamp;
        System.out.println(output);
        dataOutput(output);
    }

    private void dataOutput(String output_data) { //Zapisuje data do souboru dataPocasi.txt
        File file = new File("dataPocasi.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!file.exists()){
                file.createNewFile();
            }
            System.out.println("Zapisuji data do souboru dataPocasi.txt");
            writer.write("\n"+output_data);

        } catch (IOException e) {
            System.out.println("Chyba při zápisu do souboru");
        }

    }


    private static double round(double value, int precision) { //Zaokrouhluje čísla
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private void nameConvert() { //Převádí název lokace na formát který je použitelný v URL
        user_location = user_location.replace(" ", ".");
        user_location = user_location.toLowerCase();
        APICallCords(user_location);
    }


    public void APICallWeather(double lat, double lon) { //Volá API a získává data o počasí z funkce ApiCallCords
        final String APIKEY = "7fcce5dabf80a14dd91a0dc3ec527e3b";
        final String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + APIKEY + "&units=metric&lang=cz";
        final String newUrl = "https://api.openweathermap.org/data/3.0/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + APIKEY + "&units=metric&lang=cz";


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) { //Vytváří HTTP klienta
            try { //Získává data z API
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String weatherResponse = EntityUtils.toString(response.getEntity());
                    parseWeatherResponse(weatherResponse,user_location);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try { //Zavírá HTTP klienta
                    httpClient.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void APICallCords(String user_location) { //Volá API a získává souřadnice lokace které předává funkci APICallWeather
        final String APIKEY = "7fcce5dabf80a14dd91a0dc3ec527e3b";
        final String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + URLEncoder.encode(user_location, StandardCharsets.UTF_8) + "&limit=1&appid=" + APIKEY;


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try {
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    parseJsonResponse(jsonResponse);
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






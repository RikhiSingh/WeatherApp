import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retrieve weather data from API
// data from API and return it
public class WeatherApp {
    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        // get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: No location data found for " + locationName);
            return null;
        }

        // extract lat and long
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // api request url with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude="+ longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try{
            // call api
            HttpURLConnection conn = fetchAPIResponse(urlString);

            //check HTTP request status code
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();
            conn.disconnect();

            //parse through data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retreive hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            if (hourly == null) {
                System.out.println("Error: No hourly data found");
                return null;
            }

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            if (temperatureData == null) {
                System.out.println("Error: No temperature data found");
                return null;
            }
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            if (weatherCode == null) {
                System.out.println("Error: No weather code data found");
                return null;
            }
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            if (relativeHumidity == null) {
                System.out.println("Error: No humidity data found");
                return null;
            }
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            if (windSpeedData == null) {
                System.out.println("Error: No wind speed data found");
                return null;
            }
            double windSpeed = (double) windSpeedData.get(index);

            JSONObject weatherData  = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //retrieve location coordinates for given location name
    public static JSONArray getLocationData(String locationName){
        // replace whitespace in locaiton to + to match API request format
        // name=new+york
        locationName = locationName.replaceAll(" ","+");

        // build API url
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                            locationName + "&count=10&language=en&format=json";

        try{
            // call api and get the response
            HttpURLConnection conn = fetchAPIResponse(urlString);

            // cehck response status
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                // store API result
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                //read and store data into stringbuilder
                while (scanner.hasNext()){
                    resultJSON.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close url connection
                conn.disconnect();

                // parse JSON string to JSON Object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

                // get the list of location data the API generated from location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // cant find the location
        return null;
    }

    private static HttpURLConnection fetchAPIResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // conenct to API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        //cant make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //fetch current one from time list
        for (int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2024-01-01T00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // convert weather code to readable form
    private static String convertWeatherCode(long weatherCode){
            String weatherCondition = "";
            if(weatherCode == 0L){
                weatherCondition = "Clear";
            } else if (weatherCode > 0L && weatherCode <= 3L) {
                weatherCondition = "Cloudy";
            } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)) {
                weatherCondition = "Rain";
            } else if (weatherCode >= 71 & weatherCode <= 77L){
                weatherCondition = "Snow";
            }

            return weatherCondition;
    }
}

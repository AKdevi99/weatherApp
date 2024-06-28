import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class weatherapp {
    public static JSONObject getWeatherData(String locationname) {
        JSONArray locationData = getLocationData(locationname);
        //extract the coordinate longtitude and latitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (Double) location.get("latitude");
        double longitude = (Double) location.get("longitude");

        String url="https://api.open-meteo.com/v1/forecast?" +
                "latitude="+latitude+"&longitude="+longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try{
            //call the api
            HttpURLConnection con = fetchApiResponse(url);

            if(con.getResponseCode() != 200) {
                System.out.println("Error:could not connect to API");
                return null;
            }else
            {
                //storing the result data
                StringBuilder result = new StringBuilder();
                Scanner scanner = new Scanner(con.getInputStream());

                while(scanner.hasNext()){
                    result.append(scanner.nextLine());
                }

                scanner.close();
                con.disconnect();

                //create parse
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(result.toString());
                JSONObject hourly = (JSONObject) jsonObject.get("hourly");

                //getting data from current time
                JSONArray time =(JSONArray) hourly.get("time");
                int index = FindIndexOfCurrentTime(time);


                //get relative data forcast data to our current hour
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (Double) temperatureData.get(index);

                //get weather code
                JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

                //get humidity
                JSONArray relativehumidity = (JSONArray) hourly.get("relative_humidity_2m");
                long humidity = (Long) relativehumidity.get(index);

                //get windspeed
                JSONArray windspeeddata = (JSONArray)hourly.get("wind_speed_10m");
                double Windspeed= (Double) windspeeddata.get(index);

                //creating the weather json object to access it from frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weatherCondition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed",Windspeed );

                return weatherData;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationname) {
        locationname = locationname.replaceAll(" ","+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+locationname+"&count=10&language=en&format=json";

        try{
            HttpURLConnection con = fetchApiResponse(urlString);
            //check the status
            if(con.getResponseCode() != 200){
                System.out.println("Error:could not connect to API");
                return null;
            }else {
                //storing api results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(con.getInputStream());

                while(scanner.hasNext())
                {   //read and store the data into our string builder
                    resultJson.append(scanner.nextLine());
                }
                //close the scanner
                scanner.close();
                //close the connection
                con.disconnect();

                //creating a parser
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(resultJson.toString());

                JSONArray locationData =(JSONArray) jsonObject.get("results");
                return locationData;

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) throws Exception{

        try{
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.connect();
            return con;
        }catch(Exception e){
            e.printStackTrace();
        }
    return null;

    }

    private static int FindIndexOfCurrentTime(JSONArray timelist){
        String currentTime = getCurrentTime();

        for(int i=0;i<timelist.size();i++)
        {
            String time = (String) timelist.get(i);
            if(time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        //get the current date and time
        LocalDateTime currentdatetime = LocalDateTime.now();

        //format date to be 2024-06-02T00:00(api format)
        //creating a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentdatetime.format(formatter);

        return formattedDateTime;


    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition ="";

        if(weatherCode ==0L){
            weatherCondition = "Sunny";
        }else if(weatherCode > 0L && weatherCode <= 3L){
            weatherCondition = "Cloudy";
        }else if((weatherCode >=51L && weatherCode <=67L) || (weatherCode >=80L && weatherCode <=99L)){
            weatherCondition = "Rainy";
        } else if (weatherCode >= 71L && weatherCode <=77 ) {
            weatherCondition = "Snow";
            
        }

        return weatherCondition;
    }
}

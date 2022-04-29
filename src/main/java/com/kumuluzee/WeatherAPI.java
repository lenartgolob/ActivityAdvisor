package com.kumuluzee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequestScoped
public class WeatherAPI {

    private String apiKey = ConfigurationUtil.getInstance().get("kumuluzee.api-key").orElse(null);

    private ObjectMapper objectMapper = new ObjectMapper();

    public WeatherAPI(){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Gets temperature from latitude and longitude
    public Double getTemp(double lat, double lng) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://weatherbit-v1-mashape.p.rapidapi.com/current?lon=" + lng + "&lat=" + lat))
                .header("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
                .header("x-rapidapi-key", apiKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonWeather = new JSONObject(response.body());
        JSONArray jsonArrayWeather = (JSONArray) jsonWeather.get("data");
        JSONObject weather = (JSONObject) jsonArrayWeather.get(0);
        Double temp;
        if(weather.get("temp") instanceof Integer){
            Integer tempInteger = (Integer) weather.get("temp");
            temp = tempInteger.doubleValue();
        } else {
            temp = (Double) weather.get("temp");
        }
/*
        BigDecimal temp = BigDecimal.valueOf((Double) weather.get("temp"));
*/
        return temp;
    }
}

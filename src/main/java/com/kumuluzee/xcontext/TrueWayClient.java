package com.kumuluzee.xcontext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluzee.xcontext.APIResponses.ReverseGeocode;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RequestScoped
public class TrueWayClient {

    private String apiKey = ConfigurationUtil.getInstance().get("kumuluzee.api-key").orElse(null);

    private ObjectMapper objectMapper = new ObjectMapper();

    public TrueWayClient(){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Gets a destination from TrueWay API
    public String getTrueWay(double lat, double lng, String type, int radius) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://trueway-places.p.rapidapi.com/FindPlacesNearby?location=" + lat + "%2C" + lng + "&type=" + type + "&radius=" + radius + "&language=en"))
                .header("x-rapidapi-host", "trueway-places.p.rapidapi.com")
                .header("x-rapidapi-key", apiKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Gets address from latitude and longitude from TrueWay API
    public String reverseGeocode(double lat, double lng) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://trueway-geocoding.p.rapidapi.com/ReverseGeocode?location=" + lat + "%2C" + lng + "&language=en"))
                .header("x-rapidapi-host", "trueway-geocoding.p.rapidapi.com")
                .header("x-rapidapi-key", apiKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        List<ReverseGeocode> addresses = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<ReverseGeocode>>(){});
        return addresses.get(0).getAddress();
    }
}

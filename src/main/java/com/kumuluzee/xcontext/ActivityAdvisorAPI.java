package com.kumuluzee.xcontext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluzee.xcontext.APIResponses.Restaurant;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;


@RequestScoped
public class ActivityAdvisorAPI {

    @Inject
    private XContext xContext;

    public ActivityResponse getRestaurant() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Random rand = new Random();
        int i;

/*        for(Restaurant r : restaurants){
            r.setDistance(distance(xContext.getContext().getLocation().getLatitude(), r.getLatitude(), xContext.getContext().getLocation().getLongitude(), r.getLongitude(), 0.0, 0.0));
        }*/
        if(xContext.getContext().getBatteryPercentage() > 30){
            // Searches restaurants in 10km vacinity
            JSONObject json = new JSONObject(getRestaurants(10));
            List<Restaurant> restaurants = objectMapper.readValue(json.getJSONArray("data").toString(), new TypeReference<List<Restaurant>>(){});
            for(Restaurant r : restaurants){
            }
            do{
                i = rand.nextInt(restaurants.size());
            }while (restaurants.get(i).getName() == null && restaurants.get(i).getAddress() == null);
            ActivityResponse activityResponse = new ActivityResponse("I hope you're hungry, because Activity Advisor found a restaurant called " + restaurants.get(i).getName() + ". The restaurant is located on " + restaurants.get(i).getAddress() + " and don't worry, you've got enough juice to make the trip!");
            return activityResponse;
        } else {
            // Searches restaurants in 1km vacinity
            JSONObject json = new JSONObject(getRestaurants(1));
            List<Restaurant> restaurants = objectMapper.readValue(json.getJSONArray("data").toString(), new TypeReference<List<Restaurant>>(){});
            do{
                i = rand.nextInt(restaurants.size());
            }while (restaurants.get(i).getName() == null && restaurants.get(i).getAddress() == null);
            ActivityResponse activityResponse = new ActivityResponse("I hope you're hungry, because Activity Advisor found a restaurant called " + restaurants.get(i).getName() + ". The restaurant is located on " + restaurants.get(i).getAddress() + " and don't worry, you've got enough juice to make the trip!");
            return activityResponse;

        }
    }

    public String getRestaurants(int distance) throws Exception{
        System.out.println("meljem...");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://travel-advisor.p.rapidapi.com/restaurants/list-by-latlng?latitude=" + xContext.getContext().getLocation().getLatitude() + "&longitude=" + xContext.getContext().getLocation().getLongitude() + "&limit=20&currency=EUR&distance=" + distance + "&open_now=false&lunit=km&lang=en_US"))
                .header("x-rapidapi-host", "travel-advisor.p.rapidapi.com")
                .header("x-rapidapi-key", "67a73ab195msh9badd398a85a65bp11b277jsna7322fef16c1")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Distance in meters, el = altitude
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}

package com.kumuluzee.xcontext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluzee.xcontext.APIResponses.Restaurant;
import com.kumuluzee.xcontext.APIResponses.TrueWayResponse;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;


@RequestScoped
public class ActivityAdvisorAPI {

    @Inject
    private XContext xContext;

    public ActivityResponse getActivity() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        List<TrueWayResponse> destinations = new ArrayList<TrueWayResponse>();
        if(xContext.getContext().getLocation() != null){
            if(xContext.getContext().getTime() != null){
                System.out.println(getActivityBasedOnTime());
            }

        } else {

        }
        ActivityResponse activity = new ActivityResponse("neki");
        return activity;
    }

    public String timePeriod() throws Exception{
        String morningUpperEdge = "10:00:01";
        String noonUpperEdge = "15:00:01";
        String eveningUpperEdge = "20:00:01";
        String nightUpperEdge = "23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timeOnly = sdf.format(xContext.getContext().getTime());

        Date timeMorning = sdf.parse(morningUpperEdge);
        Date timeNoon = sdf.parse(noonUpperEdge);
        Date timeEvening = sdf.parse(eveningUpperEdge);
        Date timeNight = sdf.parse(nightUpperEdge);
        Date timeContext = sdf.parse(timeOnly);

        Calendar calendarMorning = Calendar.getInstance();
        calendarMorning.setTime(timeMorning);
        calendarMorning.add(Calendar.DATE, 1);

        Calendar calendarNoon = Calendar.getInstance();
        calendarNoon.setTime(timeNoon);
        calendarNoon.add(Calendar.DATE, 1);

        Calendar calendarEvening = Calendar.getInstance();
        calendarEvening.setTime(timeEvening);
        calendarEvening.add(Calendar.DATE, 1);

        Calendar calendarNight = Calendar.getInstance();
        calendarNight.setTime(timeNight);
        calendarNight.add(Calendar.DATE, 1);

        Calendar calendarContext = Calendar.getInstance();
        calendarContext.setTime(timeContext);
        calendarContext.add(Calendar.DATE, 1);

        Date x = calendarContext.getTime();
        String timePeriod = "";
        if (x.before(calendarMorning.getTime())) {
            timePeriod = "Morning";
        }
        else if(x.after(calendarMorning.getTime()) && x.before(calendarNoon.getTime())){
            timePeriod = "Noon";
        }
        else if(x.after(calendarNoon.getTime()) && x.before(calendarEvening.getTime())){
            timePeriod = "Evening";
        }
        else if(x.after(calendarEvening.getTime()) && x.before(calendarNight.getTime())){
            timePeriod = "Night";
        }
        return timePeriod;
    }

    public String getTrueWay(double lat, double lng, String type, int radius) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://trueway-places.p.rapidapi.com/FindPlacesNearby?location=" + lat + "%2C" + lng + "&type=" + type + "&radius=" + radius + "&language=en"))
                .header("x-rapidapi-host", "trueway-places.p.rapidapi.com")
                .header("x-rapidapi-key", "67a73ab195msh9badd398a85a65bp11b277jsna7322fef16c1")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getActivityBasedOnTime() throws Exception{
        int radius;
        if(xContext.getContext().getBatteryPercentage() == null){
            radius = 10000;
        } else {
            if(xContext.getContext().getBatteryPercentage()>30){
                radius = 10000;
            } else {
                radius = 2000;
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<TrueWayResponse> destinations = new ArrayList<TrueWayResponse>();
        Random rand = new Random();
        int randType=0, randResult;
        String timePeriod = timePeriod();
        if(timePeriod.equals("Morning")){
            ArrayList<String> morningActivities = new ArrayList<String>();
            morningActivities.add("tourist_attraction");
            morningActivities.add("cafe");
            morningActivities.add("amusement_park");
            morningActivities.add("gym");
            morningActivities.add("spa");
            while(morningActivities.size()>0){
                randType = rand.nextInt(morningActivities.size());
                JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), morningActivities.get(randType), radius));
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
                if(destinations.size()>0){
                    break;
                } else {
                    morningActivities.remove(randType);
                }
            }
            if(morningActivities.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String msg = "";
                switch(morningActivities.get(randType)){
                    case "tourist_attraction":
                        msg = "Start your day with an adventure and visit a tourist attraction " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "cafe":
                        msg = "Nothing like a cup of coffe in the morning, so visit café " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "amusement_park":
                        msg = "Start your day with an adventure and visit an amusement park " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "spa":
                        msg = "Let me guess, you could use a day off. Relax and visit a local spa " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any morning activities for you, try again later.";
            }
        }
        else if(timePeriod.equals("Noon")){
            ArrayList<String> noonActivities = new ArrayList<String>();
            noonActivities.add("restaurant");
            noonActivities.add("aquarium");
            noonActivities.add("gym");
            noonActivities.add("art-gallery");
            noonActivities.add("museum");
            while(noonActivities.size()>0){
                randType = rand.nextInt(noonActivities.size());
                JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), noonActivities.get(randType), radius));
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
                if(destinations.size()>0){
                    break;
                } else {
                    noonActivities.remove(randType);
                }
            }
            if(noonActivities.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String msg = "";
                switch(noonActivities.get(randType)){
                    case "restaurant":
                        msg = "You are not a robot right? Then you like good food so visit restaurant " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "aquarium":
                        msg = "Who doesn't want to see a shark? Visit your local aquarium " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "art-gallery":
                        msg = "Go to an art-gallery and admire some artistic masterpieces in " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "museum":
                        msg = "There's nothing wrong with broadening your horizons so you should visit museum " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any noon activities for you, try again later.";
            }

        }
        else if(timePeriod.equals("Evening")){
            ArrayList<String> eveningActivities = new ArrayList<String>();
            eveningActivities.add("bowling");
            eveningActivities.add("restaurant");
            eveningActivities.add("cinema");
            eveningActivities.add("gym");
            while(eveningActivities.size()>0){
                randType = rand.nextInt(eveningActivities.size());
                JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), eveningActivities.get(randType), radius));
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
                if(destinations.size()>0){
                    break;
                } else {
                    eveningActivities.remove(randType);
                }
            }
            if(eveningActivities.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String msg = "";
                switch(eveningActivities.get(randType)){
                    case "restaurant":
                        msg = "You are not a robot right? Then you like good food so visit restaurant " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "bowling":
                        msg = "I bet you haven't bowled in quite some time! You are missing out so go on and head to your local bowling alley " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "cinema":
                        msg = "You had a long and exhausting day and want to relax? Sounds good, visit your local cinema " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress()  + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any evening activities for you, try again later.";
            }

        }
        else if(timePeriod.equals("Night")){
            ArrayList<String> nightActivities = new ArrayList<String>();
            nightActivities.add("night_club");
            nightActivities.add("casino");
            while(nightActivities.size()>0){
                randType = rand.nextInt(nightActivities.size());
                JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), nightActivities.get(randType), radius));
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
                if(destinations.size()>0){
                    break;
                } else {
                    nightActivities.remove(randType);
                }
            }
            if(nightActivities.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String msg = "";
                switch(nightActivities.get(randType)){
                    case "night_club":
                        msg = "You want to blow of some steam? Visit a night club " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                    case "casino":
                        msg = "If you are a gambler you should visit your local casino " + activityDestination.getName() + ". It's located on " + activityDestination.getAddress() + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any night life activities for you, try again later.";
            }

        }
        return "I couldn't find any morning activities for you, try again later.";
    }

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

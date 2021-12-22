package com.kumuluzee.xcontext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluzee.xcontext.APIResponses.ReverseGeocode;
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
        if(xContext.getContext().getSteps() == null){
            System.out.println("aloooo");
        } else {
            System.out.println("yessir");
        }
        // Lokacija je
        if(xContext.getContext().getLocation() != null){
            // Korakov ni
            if(xContext.getContext().getSteps() == null){
                // Čas je
                if(xContext.getContext().getTime() != null){
                    return new ActivityResponse(getActivityBasedOnTime());
                }
                // Časa ni, temperatura je
                else if(xContext.getContext().getTemperature() != null){
                    return new ActivityResponse(getActivityBasedOnTemperature());
                } else {
                    return new ActivityResponse(getActivityBasedOnLocation());
                }
            }
            // Koraki so
            else {
                // Korakov je veliko
                if(xContext.getContext().getSteps() > 10000){
                    return new ActivityResponse(getActivityStepsHigh());
                } else { // Zanemarimo korake, saj niso ekstremi
                    // Čas je
                    if(xContext.getContext().getTime() != null){
                        return new ActivityResponse(getActivityBasedOnTime());
                    }
                    // Časa ni, temperatura je
                    else if(xContext.getContext().getTemperature() != null){
                        return new ActivityResponse(getActivityBasedOnTemperature());
                    } else {
                        return new ActivityResponse(getActivityBasedOnLocation());
                    }
                }
            }

        }
        // Lokacije ni
        else {
            // Korakov ni
            if(xContext.getContext().getSteps() == null){
                if(xContext.getContext().getTime() != null){
                    return new ActivityResponse(getActivityWithoutLocation());
                }
            }
            // Koraki so
            else {
                if(xContext.getContext().getSteps() > 10000){
                    return new ActivityResponse("I see you've been very active today, so you deserve some relaxation. I recommend to finally watch that movie you downloaded, but never ended up watching it.");
                } else {
                    return new ActivityResponse(getActivityWithoutLocation());
                }
            }

        }
        return new ActivityResponse("I couldn't find any activity for you.");
    }

    public String getActivityWithoutLocation() throws Exception{
        String timePeriod = timePeriod();
        String msg = "I couldn't find any activity for you.";
        // Če poznamo čas
        if(xContext.getContext().getTime() != null){
            if(xContext.getContext().getTemperature() != null){
                if(xContext.getContext().getTemperature() > 10){
                    switch(timePeriod){
                        case "Morning":
                            msg = "The weather is nice and warm so it would be a shame if you stayed inside all day. Go take a hike in nature.";
                            break;
                        case "Noon":
                            msg = "It's about to be lunch time, because the weather is so nice I recommend you go for a walk and stop at a local restaurant.";
                            break;
                        case "Evening":
                            msg = "You still have a little bit of time before darkness and since it's still warm go for a walk.";
                            break;
                        case "Night":
                            msg = "The weather is nice so today is the perfect day for stargazing!";
                            break;
                    }
                    if(xContext.getContext().getBatteryPercentage()<=30){
                        msg = msg + " And don't wonder off to far, because your battery is running low.";
                    }
                } else {
                    switch(timePeriod){
                        case "Morning":
                            msg = "Since it's cold outside I recommend you start your day with a cup of coffee and some reading.";
                            break;
                        case "Noon":
                            msg = "You probably don't wanna be walking around and searching for a restaurant, because it's cold outside. Just head to your fridge and there is definitely something you can cook for yourself in there.";
                            break;
                        case "Evening":
                            msg = "You should head to your local café for a cup of hot tea and catch up with your friends.";
                            break;
                        case "Night":
                            msg = "You probably want to crawl under your blanket because of this cold weather. Well that is exactly what you should do, and don't forget to put on your favourite winter movie.";
                            break;
                    }
                }
            } else {
                switch(timePeriod){
                    case "Morning":
                        msg = "Start your day with a cold shower and a book. That will set up the rest of your day perfectly.";
                        break;
                    case "Noon":
                        msg = "It's about to be lunch time, I recommend you to visit a local restaurant.";
                        break;
                    case "Evening":
                        msg = "You should go for a drink and catch up with your friends.";
                        break;
                    case "Night":
                        msg = "Take some time off and finally watch that movie you downloaded, but never ended up watching it.";
                        break;
                }
                if(xContext.getContext().getBatteryPercentage()<=30 && (timePeriod.equals("Noon") || timePeriod.equals("Evening"))){
                    msg = msg + " And don't wonder off to far, because your battery is running low.";
                }
            }
        }
        return msg;
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

    // Gets a destination from TrueWay API
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

    // Gets address from latitude and longitude from TrueWay API
    public String reverseGeocode(double lat, double lng) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://trueway-geocoding.p.rapidapi.com/ReverseGeocode?location=" + lat + "%2C" + lng + "&language=en"))
                .header("x-rapidapi-host", "trueway-geocoding.p.rapidapi.com")
                .header("x-rapidapi-key", "67a73ab195msh9badd398a85a65bp11b277jsna7322fef16c1")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        List<ReverseGeocode> addresses = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<ReverseGeocode>>(){});
        return addresses.get(0).getAddress();
    }

    // Prilagojen tudi na baterijo in temperaturo
    public String getActivityBasedOnTime() throws Exception{
        int radius;
        // Changes radius of results based on battery %
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
            if(xContext.getContext().getTemperature() != null){
                // Če je toplo, dodamo več zunanjih aktivnosti v seznam, da je večja verjetnost da izbere te.
                if(xContext.getContext().getTemperature()>10){
                    morningActivities.add("tourist_attraction");
                    morningActivities.add("amusement_park");
                    morningActivities.add("tourist_attraction");
                    morningActivities.add("amusement_park");
                }
                // Če pa je mrzlo pa dodamo več notranjih aktivnosti
                else {
                    morningActivities.add("cafe");
                    morningActivities.add("gym");
                    morningActivities.add("spa");
                    morningActivities.add("cafe");
                    morningActivities.add("gym");
                    morningActivities.add("spa");
                }
            }
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
                String address;
                if(activityDestination.getAddress() == null){
                    address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(morningActivities.get(randType)){
                    case "tourist_attraction":
                        msg = "Start your day with an adventure and visit a tourist attraction " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "cafe":
                        msg = "Nothing like a cup of coffe in the morning, so visit café " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "amusement_park":
                        msg = "Start your day with an adventure and visit an amusement park " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "spa":
                        msg = "Let me guess, you could use a day off. Relax and visit a local spa " + activityDestination.getName() + ". It's located on " + address + ".";
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
            noonActivities.add("art_gallery");
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
                String address;
                if(activityDestination.getAddress() == null){
                    address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(noonActivities.get(randType)){
                    case "restaurant":
                        msg = "You are not a robot right? Then you like good food so visit restaurant " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "aquarium":
                        msg = "Who doesn't want to see a shark? Visit your local aquarium " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "art_gallery":
                        msg = "Go to an art-gallery and admire some artistic masterpieces in " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "museum":
                        msg = "There's nothing wrong with broadening your horizons so you should visit museum " + activityDestination.getName() + ". It's located on " + address + ".";
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
                String address;
                if(activityDestination.getAddress() == null){
                    address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(eveningActivities.get(randType)){
                    case "restaurant":
                        msg = "You are not a robot right? Then you like good food so visit restaurant " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "bowling":
                        msg = "I bet you haven't bowled in quite some time! You are missing out so go on and head to your local bowling alley " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "cinema":
                        msg = "You had a long and exhausting day and want to relax? Can't really beat a good movie in your local cinema " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + address  + ".";
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
                String address;
                if(activityDestination.getAddress() == null){
                    address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(nightActivities.get(randType)){
                    case "night_club":
                        msg = "You want to blow of some steam? Visit a night club " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "casino":
                        msg = "If you are a gambler you should visit your local casino " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any night life activities for you, try again later.";
            }
        }
        return "I couldn't find any activities for you, try again later.";
    }

    public String getActivityBasedOnLocation() throws Exception{
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
        ArrayList<String> activities = new ArrayList<String>();
        activities.add("amusement_park");
        activities.add("aquarium");
        activities.add("art_gallery");
        activities.add("bowling");
        activities.add("cafe");
        activities.add("casino");
        activities.add("cinema");
        activities.add("gym");
        activities.add("museum");
        activities.add("night_club");
        activities.add("restaurant");
        activities.add("spa");
        activities.add("tourist_attraction");
        while(activities.size()>0){
            randType = rand.nextInt(activities.size());
            System.out.println("tip: " + randType);
            JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activities.get(randType), radius));
            if(json == null){
                activities.remove(randType);
                continue;
            }
            destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
            if(destinations.size()>0){
                break;
            } else {
                activities.remove(randType);
            }
        }
        if(activities.size()>0){
            randResult = rand.nextInt(destinations.size());
            System.out.println("index: " + randResult);
            TrueWayResponse activityDestination = destinations.get(randResult);
            String address;
            if(activityDestination.getAddress() == null){
                address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
            } else {
                address = activityDestination.getAddress();
            }
            String msg = "";
            switch(activities.get(randType)){
                case "amusement_park":
                    msg = "Go on an adventure and visit an amusement park " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "aquarium":
                    msg = "Who doesn't want to see a shark? Visit your local aquarium " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "art_gallery":
                    msg = "Go to an art-gallery and admire some artistic masterpieces in " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "bowling":
                    msg = "I bet you haven't bowled in quite some time! You are missing out so go on and head to your local bowling alley " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "cafe":
                    msg = "Nothing like a cup of coffe in the morning, so visit café " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "casino":
                    msg = "If you are a gambler you should visit your local casino " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "cinema":
                    msg = "You had a long and exhausting day and want to relax? Can't really beat a good movie in your local cinema " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "gym":
                    msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "museum":
                    msg = "There's nothing wrong with broadening your horizons so you should visit museum " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "night_club":
                    msg = "You want to blow of some steam? Visit a night club " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "restaurant":
                    msg = "You are not a robot right? Then you like good food so visit restaurant " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "spa":
                    msg = "Let me guess, you could use a day off. Relax and visit a local spa " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "tourist_attraction":
                    msg = "Go on an adventure and visit a tourist attraction " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
            }
            return msg;
        } else {
            return "I couldn't find any activities near you.";
        }
    }

    public String getActivityStepsHigh() throws Exception {
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
        ArrayList<String> activities = new ArrayList<String>();
        activities.add("cinema");
        activities.add("restaurant");
        activities.add("spa");
        while(activities.size()>0){
            randType = rand.nextInt(activities.size());
            System.out.println("tip: " + randType);
            JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activities.get(randType), radius));
            if(json == null){
                activities.remove(randType);
                continue;
            }
            destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
            if(destinations.size()>0){
                break;
            } else {
                activities.remove(randType);
            }
        }
        if(activities.size()>0){
            randResult = rand.nextInt(destinations.size());
            System.out.println("index: " + randResult);
            TrueWayResponse activityDestination = destinations.get(randResult);
            String address;
            if(activityDestination.getAddress() == null){
                address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
            } else {
                address = activityDestination.getAddress();
            }
            String msg = "";
            switch(activities.get(randType)){
                case "cinema":
                    msg = "I see you've been very active today, so you deserve some off-time. Visit your local cinema " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "restaurant":
                    msg = "I see you've been very active today, so I bet you wouldn't say no to a good meal in " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
                case "spa":
                    msg = "You deserve some relaxation after the day you just had! Visit your local spa " + activityDestination.getName() + ". It's located on " + address + ".";
                    break;
            }
            return msg;
        } else {
            return "I couldn't find any relaxing activities near you.";
        }
    }

    public String getActivityBasedOnTemperature() throws Exception {
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
        if(xContext.getContext().getTemperature() > 10){
            ArrayList<String> activitiesHot = new ArrayList<String>();
            activitiesHot.add("amusement_park");
            activitiesHot.add("tourist_attraction");
            while(activitiesHot.size()>0){
                randType = rand.nextInt(activitiesHot.size());
                JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activitiesHot.get(randType), radius));
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
                if(destinations.size()>0){
                    break;
                } else {
                    activitiesHot.remove(randType);
                }
            }
            if(activitiesHot.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String address;
                if(activityDestination.getAddress() == null){
                    address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(activitiesHot.get(randType)){
                    case "amusement_park":
                        msg = "Since the weather is so nice outside it would be a shame to sit inside all day, so start your day with an adventure and visit an amusement park  " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "tourist_attraction":
                        msg = "Since the weather is so nice outside it would be a shame to sit inside all day, so start your day with an adventure and visit a tourist attraction  " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any outside activities for you.";
            }
        } else {
            ArrayList<String> activitiesCold = new ArrayList<String>();
            activitiesCold.add("aquarium");
            activitiesCold.add("art_gallery");
            activitiesCold.add("bowling");
            activitiesCold.add("cafe");
            activitiesCold.add("gym");
            activitiesCold.add("museum");
            activitiesCold.add("restaurant");
            activitiesCold.add("spa");
            while(activitiesCold.size()>0){
                randType = rand.nextInt(activitiesCold.size());
                JSONObject json = new JSONObject(getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activitiesCold.get(randType), radius));
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
                if(destinations.size()>0){
                    break;
                } else {
                    activitiesCold.remove(randType);
                }
            }
            if(activitiesCold.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String address;
                if(activityDestination.getAddress() == null){
                    address = reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(activitiesCold.get(randType)){
                    case "aquarium":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local aquarium " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "art_gallery":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local art gallery " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "bowling":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local bowling alley " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "cafe":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local café " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "gym":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local gym " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "museum":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local museum " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "restaurant":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your a nearby restaurant " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                    case "spa":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local spa " + activityDestination.getName() + ". It's located on " + address + ".";
                        break;
                }
                return msg;
            } else {
                return "I couldn't find any night life activities for you, try again later.";
            }

        }


    }
}

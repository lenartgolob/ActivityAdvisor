package com.kumuluzee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kumuluzee.APIResponses.TrueWayResponse;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalTime;
import java.util.*;


@RequestScoped
public class ActivityAdvisorService {

    @Inject
    private XContext xContext;

    @Inject
    private TrueWayClient trueWayBean;

    @Inject
    private WeatherAPI weatherBean;

    @Inject
    private GoogleMapsClient googleMapsBean;

    private ObjectMapper objectMapper = new ObjectMapper();

    public ActivityAdvisorService(){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ActivityResponse getActivity() throws Exception{
        // Changes coordinates based on placeId
        if(xContext.getContext().getPlaceId() != null) {
            Context context = xContext.getContext();
            com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Location placeLocation = googleMapsBean.getCoordinates(xContext.getContext().getPlaceId()).getResult().getGeometry().getLocation();
            Location location = new Location();
            location.setLatitude(placeLocation.getLat());
            location.setLongitude(placeLocation.getLng());
            context.setLocation(location);
            xContext.setContext(context);
        }
        // Lokacija je
        if(xContext.getContext().getLocation() != null){
            if(xContext.getContext().getTemperature() == null){
                // Gets temperature from WeatherAPI with coordinates
                Double temp = weatherBean.getTemp(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude());
                xContext.getContext().setTemperature(temp);
            }
            // Korakov ni
            if(xContext.getContext().getSteps() == null){
                // ??as je
                if(xContext.getContext().getTime() != null){
                    return getActivityBasedOnTime();
                }
                // ??asa ni, temperatura je
                else if(xContext.getContext().getTemperature() != null){
                    return getActivityBasedOnTemperature();
                } else {
                    return getActivityBasedOnLocation();
                }
            }
            // Koraki so
            else {
                // Korakov je veliko
                if(xContext.getContext().getSteps() > 10000){
                    return getActivityStepsHigh();
                } else { // Zanemarimo korake, saj niso ekstremi
                    // ??as je
                    if(xContext.getContext().getTime() != null){
                        return getActivityBasedOnTime();
                    }
                    // ??asa ni, temperatura je
                    else if(xContext.getContext().getTemperature() != null){
                        return getActivityBasedOnTemperature();
                    } else {
                        return getActivityBasedOnLocation();
                    }
                }
            }

        }
        // Lokacije ni
        else {
            // Korakov ni
            if(xContext.getContext().getSteps() == null){
                if(xContext.getContext().getTime() != null){
                    return getActivityWithoutLocation();
                }
            }
            // Koraki so
            else {
                if(xContext.getContext().getSteps() > 10000){
                    ActivityResponse activityResponse = new ActivityResponse();
                    activityResponse.setMessage("I see you've been very active today, so you deserve some relaxation. I recommend to finally watch that movie you downloaded, but never ended up watching it.");
                    return activityResponse;
                } else {
                    return getActivityWithoutLocation();
                }
            }

        }
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setMessage("I couldn't find any activity for you.");
        return activityResponse;
    }

    public ActivityResponse getActivityWithoutLocation() throws Exception{
        ActivityResponse activityResponse = new ActivityResponse();
        String msg = "I couldn't find any activity for you.";
        // ??e poznamo ??as
        if(xContext.getContext().getTime() != null){
            String timePeriod = timePeriod();
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
                    if(xContext.getContext().getBatteryPercentage() != null){
                        if(xContext.getContext().getBatteryPercentage()<=30){
                            msg = msg + " And don't wonder off to far, because your battery is running low.";
                        }
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
                            msg = "You should head to your local caf?? for a cup of hot tea and catch up with your friends.";
                            break;
                        case "Night":
                            msg = "You probably want to crawl under your blanket because of this cold weather. Well that is exactly what you should do, and don't forget to put on your favourite winter movie.";
                            break;
                    }
                    if(xContext.getContext().getBatteryPercentage() != null){
                        if(xContext.getContext().getBatteryPercentage()<=30){
                            msg = msg + " And don't wonder off to far, because your battery is running low.";
                        }
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
                if(xContext.getContext().getBatteryPercentage() != null){
                    if(xContext.getContext().getBatteryPercentage()<=30){
                        msg = msg + " And don't wonder off to far, because your battery is running low.";
                    }
                }
            }
        }
        activityResponse.setMessage(msg);
        return activityResponse;
    }

    // Prilagojen tudi na baterijo in temperaturo
    public ActivityResponse getActivityBasedOnTime() throws Exception{
        ActivityResponse activityResponse = new ActivityResponse();
        Location location = new Location();
        int radius;
        // Changes radius of results based on battery %
        if(xContext.getContext().getBatteryPercentage() == null){
            radius = 2000;
        } else {
            if(xContext.getContext().getBatteryPercentage()>30){
                radius = 2000;
            } else {
                radius = 1000;
            }
        }
        List<TrueWayResponse> destinations = new ArrayList<TrueWayResponse>();
        Random rand = new Random();
        int randType=0, randResult;
        String timePeriod = timePeriod();
        if(timePeriod.equals("Morning")){
            ArrayList<String> morningActivities = new ArrayList<String>();
            if(xContext.getContext().getTemperature() != null){
                // ??e je toplo, dodamo ve?? zunanjih aktivnosti v seznam, da je ve??ja verjetnost da izbere te.
                if(xContext.getContext().getTemperature()>10){
                    morningActivities.add("tourist_attraction");
                    morningActivities.add("amusement_park");
                    morningActivities.add("tourist_attraction");
                    morningActivities.add("amusement_park");
                }
                // ??e pa je mrzlo pa dodamo ve?? notranjih aktivnosti
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
                JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), morningActivities.get(randType), radius));
                if(!json.has("results")){
                    morningActivities.remove(randType);
                    continue;
                }
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
                    address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(morningActivities.get(randType)){
                    case "tourist_attraction":
                        msg = "Start your day with an adventure and visit a tourist attraction " + activityDestination.getName() + ".";
                        break;
                    case "cafe":
                        msg = "Nothing like a cup of coffe in the morning, so visit caf?? " + activityDestination.getName() + ".";
                        break;
                    case "amusement_park":
                        msg = "Start your day with an adventure and visit an amusement park " + activityDestination.getName() + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ".";
                        break;
                    case "spa":
                        msg = "Let me guess, you could use a day off. Relax and visit a local spa " + activityDestination.getName() + ".";
                        break;
                }
                activityResponse.setMessage(msg);
                activityResponse.setType(morningActivities.get(randType));
                activityResponse.setAddress(address);
                activityResponse.setName(activityDestination.getName());
                location.setLatitude(activityDestination.getLocation().getLat());
                location.setLongitude(activityDestination.getLocation().getLng());
                activityResponse.setLocation(location);
                return activityResponse;
            } else {
                activityResponse.setMessage("I couldn't find any morning activities for you, try again later.");
                return activityResponse;
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
                JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), noonActivities.get(randType), radius));
                if(!json.has("results")){
                    noonActivities.remove(randType);
                    continue;
                }
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
                    address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(noonActivities.get(randType)){
                    case "restaurant":
                        msg = "I know you like good food, so visit restaurant " + activityDestination.getName() + ".";
                        break;
                    case "aquarium":
                        msg = "Who doesn't want to see a shark? Visit your local aquarium " + activityDestination.getName() + ".";
                        break;
                    case "art_gallery":
                        msg = "Go to an art-gallery and admire some artistic masterpieces in " + activityDestination.getName() + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ".";
                        break;
                    case "museum":
                        msg = "There's nothing wrong with broadening your horizons so you should visit museum " + activityDestination.getName() + ".";
                        break;
                }
                activityResponse.setMessage(msg);
                activityResponse.setAddress(address);
                activityResponse.setType(noonActivities.get(randType));
                activityResponse.setName(activityDestination.getName());
                location.setLatitude(activityDestination.getLocation().getLat());
                location.setLongitude(activityDestination.getLocation().getLng());
                activityResponse.setLocation(location);
                return activityResponse;
            } else {
                activityResponse.setMessage("I couldn't find any noon activities for you, try again later.");
                return activityResponse;
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
                JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), eveningActivities.get(randType), radius));
                if(!json.has("results")){
                    eveningActivities.remove(randType);
                    continue;
                }
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
                    address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(eveningActivities.get(randType)){
                    case "restaurant":
                        msg = "I know you like good food, so visit restaurant " + activityDestination.getName() + ".";
                        break;
                    case "bowling":
                        msg = "I bet you haven't bowled in quite some time! You are missing out so go on and head to your local bowling alley " + activityDestination.getName() + ".";
                        break;
                    case "cinema":
                        msg = "You had a long and exhausting day and want to relax? Can't really beat a good movie in your local cinema " + activityDestination.getName() + ".";
                        break;
                    case "gym":
                        msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ".";
                        break;
                }
                activityResponse.setMessage(msg);
                activityResponse.setAddress(address);
                activityResponse.setType(eveningActivities.get(randType));
                activityResponse.setName(activityDestination.getName());
                location.setLatitude(activityDestination.getLocation().getLat());
                location.setLongitude(activityDestination.getLocation().getLng());
                activityResponse.setLocation(location);
                return activityResponse;
            } else {
                activityResponse.setMessage("I couldn't find any evening activities for you, try again later.");
                return activityResponse;
            }
        }
        else if(timePeriod.equals("Night")){
            ArrayList<String> nightActivities = new ArrayList<String>();
            nightActivities.add("night_club");
            nightActivities.add("casino");
            while(nightActivities.size()>0){
                randType = rand.nextInt(nightActivities.size());
                JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), nightActivities.get(randType), radius));
                if(!json.has("results")){
                    nightActivities.remove(randType);
                    continue;
                }
                destinations = objectMapper.readValue(json.getJSONArray("results").toString(), new TypeReference<List<TrueWayResponse>>(){});
            }
            if(nightActivities.size()>0){
                randResult = rand.nextInt(destinations.size());
                TrueWayResponse activityDestination = destinations.get(randResult);
                String address;
                if(activityDestination.getAddress() == null){
                    address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(nightActivities.get(randType)){
                    case "night_club":
                        msg = "You want to blow of some steam? Visit a night club " + activityDestination.getName() + ".";
                        break;
                    case "casino":
                        msg = "If you are a gambler you should visit your local casino " + activityDestination.getName() + ".";
                        break;
                }
                activityResponse.setMessage(msg);
                activityResponse.setAddress(address);
                activityResponse.setType(nightActivities.get(randType));
                activityResponse.setName(activityDestination.getName());
                location.setLatitude(activityDestination.getLocation().getLat());
                location.setLongitude(activityDestination.getLocation().getLng());
                activityResponse.setLocation(location);
                return activityResponse;
            } else {
                activityResponse.setMessage("I couldn't find any night activities for you, try again later.");
                return activityResponse;
            }
        }
        activityResponse.setMessage("I couldn't find any night activities for you, try again later.");
        return activityResponse;
    }

    public ActivityResponse getActivityBasedOnLocation() throws Exception{
        ActivityResponse activityResponse = new ActivityResponse();
        Location location = new Location();
        int radius;
        if(xContext.getContext().getBatteryPercentage() == null){
            radius = 2000;
        } else {
            if(xContext.getContext().getBatteryPercentage()>30){
                radius = 2000;
            } else {
                radius = 1000;
            }
        }
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
            JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activities.get(randType), radius));
            if(!json.has("results")){
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
            TrueWayResponse activityDestination = destinations.get(randResult);
            String address;
            if(activityDestination.getAddress() == null){
                address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
            } else {
                address = activityDestination.getAddress();
            }
            String msg = "";
            switch(activities.get(randType)){
                case "amusement_park":
                    msg = "Go on an adventure and visit an amusement park " + activityDestination.getName() + ".";
                    break;
                case "aquarium":
                    msg = "Who doesn't want to see a shark? Visit your local aquarium " + activityDestination.getName() + ".";
                    break;
                case "art_gallery":
                    msg = "Go to an art-gallery and admire some artistic masterpieces in " + activityDestination.getName() + ".";
                    break;
                case "bowling":
                    msg = "I bet you haven't bowled in quite some time! You are missing out so go on and head to your local bowling alley " + activityDestination.getName() + ".";
                    break;
                case "cafe":
                    msg = "Nothing like a cup of coffe and a chat with your friends, so visit caf?? " + activityDestination.getName() + ".";
                    break;
                case "casino":
                    msg = "If you are a gambler you should visit your local casino " + activityDestination.getName() + ".";
                    break;
                case "cinema":
                    msg = "You had a long and exhausting day and want to relax? Can't really beat a good movie in your local cinema " + activityDestination.getName() + ".";
                    break;
                case "gym":
                    msg = "Stop sitting around and visit your local gym " + activityDestination.getName() + ".";
                    break;
                case "museum":
                    msg = "There's nothing wrong with broadening your horizons so you should visit museum " + activityDestination.getName() + ".";
                    break;
                case "night_club":
                    msg = "You want to blow of some steam? Visit a night club " + activityDestination.getName() + ".";
                    break;
                case "restaurant":
                    msg = "I know you like good food, so visit restaurant " + activityDestination.getName() + ".";
                    break;
                case "spa":
                    msg = "Let me guess, you could use a day off. Relax and visit a local spa " + activityDestination.getName() + ".";
                    break;
                case "tourist_attraction":
                    msg = "Go on an adventure and visit a tourist attraction " + activityDestination.getName() + ".";
                    break;
            }
            activityResponse.setMessage(msg);
            activityResponse.setAddress(address);
            activityResponse.setType(activities.get(randType));
            activityResponse.setName(activityDestination.getName());
            location.setLatitude(activityDestination.getLocation().getLat());
            location.setLongitude(activityDestination.getLocation().getLng());
            activityResponse.setLocation(location);
        } else {
            activityResponse.setMessage("I couldn't find any activities near you.");
        }
        return activityResponse;
    }

    public ActivityResponse getActivityStepsHigh() throws Exception {
        ActivityResponse activityResponse = new ActivityResponse();
        Location location = new Location();
        int radius;
        if(xContext.getContext().getBatteryPercentage() == null){
            radius = 2000;
        } else {
            if(xContext.getContext().getBatteryPercentage()>30){
                radius = 2000;
            } else {
                radius = 1000;
            }
        }
        List<TrueWayResponse> destinations = new ArrayList<TrueWayResponse>();
        Random rand = new Random();
        int randType=0, randResult;
        ArrayList<String> activities = new ArrayList<String>();
        activities.add("cinema");
        activities.add("restaurant");
        activities.add("spa");
        while(activities.size()>0){
            randType = rand.nextInt(activities.size());
            JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activities.get(randType), radius));
            if(!json.has("results")){
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
            TrueWayResponse activityDestination = destinations.get(randResult);
            String address;
            if(activityDestination.getAddress() == null){
                address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
            } else {
                address = activityDestination.getAddress();
            }
            String msg = "";
            switch(activities.get(randType)){
                case "cinema":
                    msg = "I see you've been very active today, so you deserve some off-time. Visit your local cinema " + activityDestination.getName() + ".";
                    break;
                case "restaurant":
                    msg = "I see you've been very active today, so I bet you wouldn't say no to a good meal in " + activityDestination.getName() + ".";
                    break;
                case "spa":
                    msg = "You deserve some relaxation after the day you just had! Visit your local spa " + activityDestination.getName() + ".";
                    break;
            }
            activityResponse.setMessage(msg);
            activityResponse.setAddress(address);
            activityResponse.setType(activities.get(randType));
            activityResponse.setName(activityDestination.getName());
            location.setLatitude(activityDestination.getLocation().getLat());
            location.setLongitude(activityDestination.getLocation().getLng());
            activityResponse.setLocation(location);
        } else {
            activityResponse.setMessage("I couldn't find any relaxing activities near you.");
        }
        return activityResponse;
    }

    public ActivityResponse getActivityBasedOnTemperature() throws Exception {
        ActivityResponse activityResponse = new ActivityResponse();
        Location location = new Location();
        int radius;
        if(xContext.getContext().getBatteryPercentage() == null){
            radius = 2000;
        } else {
            if(xContext.getContext().getBatteryPercentage()>30){
                radius = 2000;
            } else {
                radius = 1000;
            }
        }
        List<TrueWayResponse> destinations = new ArrayList<TrueWayResponse>();
        Random rand = new Random();
        int randType=0, randResult;
        if(xContext.getContext().getTemperature() > 10){
            ArrayList<String> activitiesHot = new ArrayList<String>();
            activitiesHot.add("amusement_park");
            activitiesHot.add("tourist_attraction");
            while(activitiesHot.size()>0){
                randType = rand.nextInt(activitiesHot.size());
                JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activitiesHot.get(randType), radius));
                if(!json.has("results")){
                    activitiesHot.remove(randType);
                    continue;
                }
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
                    address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(activitiesHot.get(randType)){
                    case "amusement_park":
                        msg = "Since the weather is so nice outside it would be a shame to sit inside all day, so start your day with an adventure and visit an amusement park  " + activityDestination.getName() + ".";
                        break;
                    case "tourist_attraction":
                        msg = "Since the weather is so nice outside it would be a shame to sit inside all day, so start your day with an adventure and visit a tourist attraction  " + activityDestination.getName() + ".";
                        break;
                }
                activityResponse.setMessage(msg);
                activityResponse.setAddress(address);
                activityResponse.setType(activitiesHot.get(randType));
                activityResponse.setName(activityDestination.getName());
                location.setLatitude(activityDestination.getLocation().getLat());
                location.setLongitude(activityDestination.getLocation().getLng());
                activityResponse.setLocation(location);
            } else {
                activityResponse.setMessage("I couldn't find any outside activities for you.");
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
                JSONObject json = new JSONObject(trueWayBean.getTrueWay(xContext.getContext().getLocation().getLatitude(), xContext.getContext().getLocation().getLongitude(), activitiesCold.get(randType), radius));
                if(!json.has("results")){
                    activitiesCold.remove(randType);
                    continue;
                }
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
                    address = trueWayBean.reverseGeocode(activityDestination.getLocation().getLat(), activityDestination.getLocation().getLng());
                } else {
                    address = activityDestination.getAddress();
                }
                String msg = "";
                switch(activitiesCold.get(randType)){
                    case "aquarium":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local aquarium " + activityDestination.getName() + ".";
                        break;
                    case "art_gallery":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local art gallery " + activityDestination.getName() + ".";
                        break;
                    case "bowling":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local bowling alley " + activityDestination.getName() + ".";
                        break;
                    case "cafe":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local caf?? " + activityDestination.getName() + ".";
                        break;
                    case "gym":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local gym " + activityDestination.getName() + ".";
                        break;
                    case "museum":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local museum " + activityDestination.getName() + ".";
                        break;
                    case "restaurant":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your a nearby restaurant " + activityDestination.getName() + ".";
                        break;
                    case "spa":
                        msg = "Since it's cold outside I found a great activity for you that doesn't include freezing outside. Visit your local spa " + activityDestination.getName() + ".";
                        break;
                }
                activityResponse.setMessage(msg);
                activityResponse.setAddress(address);
                activityResponse.setType(activitiesCold.get(randType));
                activityResponse.setName(activityDestination.getName());
                location.setLatitude(activityDestination.getLocation().getLat());
                location.setLongitude(activityDestination.getLocation().getLng());
                activityResponse.setLocation(location);
            } else {
                activityResponse.setMessage("I couldn't find any indoor activities for you, try again later.");
            }
        }
        return activityResponse;
    }

    public String timePeriod() throws Exception{
        LocalTime morningUpperEdge = LocalTime.parse("10:00");
        LocalTime noonUpperEdge = LocalTime.parse("15:00");
        LocalTime eveningUpperEdge = LocalTime.parse("20:00");
        LocalTime nightUpperEdge = LocalTime.parse("23:59");
        LocalTime contextTime = xContext.getContext().getTime();

        String timePeriod = "";
        if (contextTime.isBefore(morningUpperEdge)) {
            timePeriod = "Morning";
        }
        else if(contextTime.isBefore(noonUpperEdge)){
            timePeriod = "Noon";
        }
        else if(contextTime.isBefore(eveningUpperEdge)){
            timePeriod = "Evening";
        }
        else if(contextTime.isBefore(nightUpperEdge)){
            timePeriod = "Night";
        }
        return timePeriod;
    }
}

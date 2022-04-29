package com.kumuluzee;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluzee.GoogleMapsResponse.GeocodeResponse.GeocodeResponse;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@RequestScoped
public class GoogleMapsClient {

    @Inject
    private XContext xContext;

    private Client client = ClientBuilder.newClient();

    private String apiKey = ConfigurationUtil.getInstance().get("kumuluzee.google-api-key").orElse(null);

    public GeocodeResponse getCoordinates(String placeID) {
        String REST_URI = "https://maps.googleapis.com/maps/api/geocode/json?place_id=" + placeID + "&key=" + apiKey;
        return client
                .target(REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .get(GeocodeResponse.class);
    }
}
package com.kumuluzee.GoogleMapsResponse.GeocodeResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodeResponse {
    private List<com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Result> results;
    private com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Result result;

    public List<com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Result> getResults() {
        return results;
    }

    public void setResults(List<com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Result> results) {
        this.results = results;
        setResult(results.get(0));
    }

    public com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Result getResult() {
        return result;
    }

    public void setResult(com.kumuluzee.GoogleMapsResponse.GeocodeResponse.Result result) {
        this.result = result;
    }
}

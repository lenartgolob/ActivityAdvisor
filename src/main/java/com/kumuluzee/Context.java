package com.kumuluzee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalTime;

/*
@RequestScoped ?
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class Context{
    private Location location;
    private Integer steps;
    private Integer batteryPercentage;
    private Double temperature;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime time;
    private String placeId;

    public Location getLocation() {
        return location;
    }

    public Integer getSteps() {
        return steps;
    }

    public Integer getBatteryPercentage() {
        return batteryPercentage;
    }

    public Double getTemperature() {
        return temperature;
    }

    public LocalTime getTime() { return time; }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public void setBatteryPercentage(Integer batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setTime(LocalTime time) { this.time = time; }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
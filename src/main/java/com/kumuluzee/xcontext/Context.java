package com.kumuluzee.xcontext;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.enterprise.context.RequestScoped;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
@RequestScoped ?
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class Context{
    private Location location;
    private Integer ambientLight;
    private Integer steps;
    private Integer batteryPercentage;
    private Integer ambientPressure;
    private Double temperature;
    private Double relativeHumidity;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss", timezone="CET")
    private Date time;

    public Location getLocation() {
        return location;
    }

    public Integer getAmbientLight() {
        return ambientLight;
    }

    public Integer getSteps() {
        return steps;
    }

    public Integer getBatteryPercentage() {
        return batteryPercentage;
    }

    public Integer getAmbientPressure() {
        return ambientPressure;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getRelativeHumidity() {
        return relativeHumidity;
    }

    public Date getTime() { return time; }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAmbientLight(Integer ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public void setBatteryPercentage(Integer batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public void setAmbientPressure(Integer ambientPressure) {
        this.ambientPressure = ambientPressure;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setRelativeHumidity(Double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public void setTime(Date time) { this.time = time; }
}
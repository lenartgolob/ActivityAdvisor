package com.kumuluzee.xcontext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.enterprise.context.RequestScoped;

/*
@RequestScoped ?
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class Context{
    private Location location;
    private Integer ambientLight;
    private Integer steps;
    private Integer batteryPercentage;
    private Gyroscope gyroscope;
    private Accelerometer accelerometer;
    private MagneticField magneticField;
    private Integer ambientPressure;
    private Double temperature;
    private Double relativeHumidity;

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

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public MagneticField getMagneticField() {
        return magneticField;
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

    public void setGyroscope(Gyroscope gyroscope) {
        this.gyroscope = gyroscope;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public void setMagneticField(MagneticField magneticField) {
        this.magneticField = magneticField;
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
}
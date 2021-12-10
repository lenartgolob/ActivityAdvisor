package com.kumuluzee.xcontext;

public class XContext{
    public Location location;
    public int ambientLight;
    public int steps;
    public int batteryPercentage;
    public int ambientPressure;
    public double temperature;
    public double relativeHumidity;

    public Location getLocation() {
        return location;
    }

    public int getAmbientLight() {
        return ambientLight;
    }

    public int getSteps() {
        return steps;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public int getAmbientPressure() {
        return ambientPressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAmbientLight(int ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public void setAmbientPressure(int ambientPressure) {
        this.ambientPressure = ambientPressure;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }
}
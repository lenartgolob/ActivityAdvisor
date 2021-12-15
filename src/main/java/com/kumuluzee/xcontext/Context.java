package com.kumuluzee.xcontext;

import javax.enterprise.context.RequestScoped;

/*
@RequestScoped ?
*/
public class Context{
    private Location location;
    private int ambientLight;
    private int steps;
    private int batteryPercentage;
    private Gyroscope gyroscope;
    private Accelerometer accelerometer;
    private MagneticField magneticField;
    private int ambientPressure;
    private double temperature;
    private double relativeHumidity;

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

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public MagneticField getMagneticField() {
        return magneticField;
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

    public void setGyroscope(Gyroscope gyroscope) {
        this.gyroscope = gyroscope;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public void setMagneticField(MagneticField magneticField) {
        this.magneticField = magneticField;
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
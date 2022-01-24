package com.kumuluzee.xcontext;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import javax.enterprise.context.RequestScoped;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

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
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime time;

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

    public LocalTime getTime() { return time; }

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

    public void setTime(LocalTime time) { this.time = time; }
}
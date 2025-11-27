package com.example.weather_app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("name")
    private String cityName;

    @SerializedName("weather")
    private List<Weather> weather; // Це список, бо погода може мати декілька станів

    @SerializedName("main")
    private MainStats mainStats;

    @SerializedName("wind")
    private Wind wind;

    // Геттери
    public String getCityName() { return cityName; }
    public List<Weather> getWeather() { return weather; }
    public MainStats getMainStats() { return mainStats; }
    public Wind getWind() { return wind; }
}
package com.example.weather_app.data.model;

import com.google.gson.annotations.SerializedName;

public class MainStats {
    @SerializedName("temp")
    private float temp;

    @SerializedName("pressure")
    private int pressure;

    @SerializedName("humidity")
    private int humidity;

    public float getTemp() { return temp; }
    public int getPressure() { return pressure; }
    public int getHumidity() { return humidity; }
}
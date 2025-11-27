package com.example.weather_app.data.model;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    private float speed;

    public float getSpeed() { return speed; }
}
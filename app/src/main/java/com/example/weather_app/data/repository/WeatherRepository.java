package com.example.weather_app.data.repository;

import com.example.weather_app.data.api.RetrofitClient;
import com.example.weather_app.data.api.WeatherApiService;
import com.example.weather_app.data.model.WeatherResponse;
import retrofit2.Call;

public class WeatherRepository {
    private final WeatherApiService apiService;

    public WeatherRepository() {
        apiService = RetrofitClient.getService();
    }

    public Call<WeatherResponse> getWeather(String city, String apiKey, String lang) {
        return apiService.getCurrentWeather(city, apiKey, "metric", lang);
    }
}
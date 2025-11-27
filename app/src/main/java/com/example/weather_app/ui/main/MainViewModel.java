package com.example.weather_app.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather_app.BuildConfig;
import com.example.weather_app.data.model.WeatherResponse;
import com.example.weather_app.data.repository.WeatherRepository;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final WeatherRepository repository;

    // LiveData для станів (спостерігаємо за ними з Activity)
    private final MutableLiveData<WeatherResponse> weatherData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MainViewModel() {
        repository = new WeatherRepository();
    }

    // Геттери для LiveData (щоб Activity могла підписатися)
    public LiveData<WeatherResponse> getWeatherData() { return weatherData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadWeather(String city) {
        isLoading.setValue(true);

        // Отримуємо мову системи (uk, en тощо), щоб сервер відповів зрозумілою мовою
        String lang = Locale.getDefault().getLanguage();

        repository.getWeather(city, BuildConfig.API_KEY, lang).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    weatherData.setValue(response.body());
                } else {
                    errorMessage.setValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed: " + t.getMessage());
            }
        });
    }
}
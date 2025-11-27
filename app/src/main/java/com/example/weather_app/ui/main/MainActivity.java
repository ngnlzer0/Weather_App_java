package com.example.weather_app.ui.main;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.weather_app.databinding.ActivityMainBinding;

import timber.log.Timber; 

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ініціалізація ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Timber.plant(new Timber.DebugTree());

        // Ініціалізація ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupObservers();
        setupListeners();

        // Завантажуємо погоду для Києва на старті
        viewModel.loadWeather("Kyiv");
    }

    private void setupListeners() {
        // 1. Логіка кнопки ПОШУКУ
        binding.btnSearch.setOnClickListener(v -> {
            if (binding.etCity.getText() != null) {
                String city = binding.etCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    viewModel.loadWeather(city);
                } else {
                    Toast.makeText(this, "Введіть назву міста", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. Логіка кнопки МОВИ
        binding.btnLangSwitch.setOnClickListener(v -> {
            // Отримуємо поточну мову
            androidx.core.os.LocaleListCompat appLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales();
            String currentLang = "";

            if (!appLocales.isEmpty()) {
                currentLang = appLocales.get(0).getLanguage();
            } else {
                currentLang = java.util.Locale.getDefault().getLanguage();
            }

            // Змінюємо: якщо en -> uk, якщо uk -> en
            String newLang = "en";
            if (currentLang.equals("en")) {
                newLang = "uk";
            }

            // Застосовуємо
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                    androidx.core.os.LocaleListCompat.forLanguageTags(newLang)
            );
        });
    }

    private void setupObservers() {

        // 1. Спостерігаємо за даними погоди
        viewModel.getWeatherData().observe(this, weatherResponse -> {
            binding.tvCityName.setText(weatherResponse.getCityName());

            // Форматуємо температуру
            if (weatherResponse.getMainStats() != null) {
                String temp = Math.round(weatherResponse.getMainStats().getTemp()) + "°C";
                binding.tvTemp.setText(temp);
            }

            // Опис та іконка
            if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
                String description = weatherResponse.getWeather().get(0).getDescription();
                binding.tvDescription.setText(description);

                String iconCode = weatherResponse.getWeather().get(0).getIcon();
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";

                Glide.with(this)
                        .load(iconUrl)
                        .into(binding.ivIcon);
            }

            // ... всередині observe ...
            if (weatherResponse.getMainStats() != null) {
                String temp = Math.round(weatherResponse.getMainStats().getTemp()) + "°C";
                binding.tvTemp.setText(temp);

                // Зберігаємо дані для віджета
                SharedPreferences prefs = getSharedPreferences("WeatherPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("city", weatherResponse.getCityName());
                editor.putString("temp", temp);

                // Беремо код іконки (наприклад "10d") зі списку
                if (!weatherResponse.getWeather().isEmpty()) {
                    editor.putString("icon_code", weatherResponse.getWeather().get(0).getIcon());
                }
                editor.apply();

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

                // Знаходимо ID наших віджетів (їх може бути кілька на екрані)
                ComponentName thisWidget = new ComponentName(this, com.example.weather_app.ui.widget.WeatherWidget.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                // Проходимось по кожному і оновлюємо
                for (int appWidgetId : appWidgetIds) {
                    com.example.weather_app.ui.widget.WeatherWidget.updateAppWidget(this, appWidgetManager, appWidgetId);
                }

                // Кажемо віджету оновитися
                android.content.Intent intent = new android.content.Intent(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.setComponent(new android.content.ComponentName(this, com.example.weather_app.ui.widget.WeatherWidget.class));
                sendBroadcast(intent);
                // -----------------------
            }
        });

        // 2. Спостерігаємо за завантаженням
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) { // Теж перевірка на всяк випадок
                if (isLoading) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSearch.setEnabled(false);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSearch.setEnabled(true);
                }
            }

        });

        // 3. Спостерігаємо за помилками
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Timber.e(error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
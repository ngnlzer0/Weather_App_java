package com.example.weather_app.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.weather_app.R;
import com.example.weather_app.ui.main.MainActivity;

public class WeatherWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Цей метод викликається, коли віджет оновлюється
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    // Метод оновлення конкретного віджета
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        String city = prefs.getString("city", "Kyiv");
        String temp = prefs.getString("temp", "--");
        String iconCode = prefs.getString("icon_code", ""); // <--- Перевір, чи є цей рядок

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        views.setTextViewText(R.id.widget_city, city);
        views.setTextViewText(R.id.widget_temp, temp);

        if (!iconCode.isEmpty()) {
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

            // Спеціальний об'єкт для віджета
            com.bumptech.glide.request.target.AppWidgetTarget awt =
                    new com.bumptech.glide.request.target.AppWidgetTarget(context.getApplicationContext(), R.id.widget_icon, views, appWidgetId);

            com.bumptech.glide.Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(iconUrl)
                    .into(awt);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
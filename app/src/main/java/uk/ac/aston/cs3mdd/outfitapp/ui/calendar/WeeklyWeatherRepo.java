package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.util.Log;

import retrofit2.Call;
import uk.ac.aston.cs3mdd.outfitapp.MainActivity;
import uk.ac.aston.cs3mdd.outfitapp.Singleton;

public class WeeklyWeatherRepo {
    private GetWeeklyWeather getWeeklyService;

    public WeeklyWeatherRepo(GetWeeklyWeather weeklyService) {
        this.getWeeklyService = weeklyService;
    }

    public Call<WeeklyWeather> getWeekly(double lat, double lon) {
        String exclude = "current,minutely,alerts";
        String units = "metric";
        Log.i(MainActivity.TAG, "Request URL: " + getWeeklyService.getWeekly(lat, lon, exclude, units, Singleton.getInstance().getWeatherKey()).request().url());
        return getWeeklyService.getWeekly(lat, lon, exclude, units, Singleton.getInstance().getWeatherKey());
    }
}

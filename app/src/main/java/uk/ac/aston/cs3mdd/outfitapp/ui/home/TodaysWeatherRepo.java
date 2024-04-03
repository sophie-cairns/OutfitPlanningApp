package uk.ac.aston.cs3mdd.outfitapp.ui.home;

import android.util.Log;

import retrofit2.Call;
import uk.ac.aston.cs3mdd.outfitapp.MainActivity;
import uk.ac.aston.cs3mdd.outfitapp.Singleton;


public class TodaysWeatherRepo {
    private GetTodaysWeather getTodaysService;

    public TodaysWeatherRepo(GetTodaysWeather todaysService) {
        this.getTodaysService = todaysService;
    }

    public Call<TodaysWeather> getForecast(double lat, double lon) {
        Log.i(MainActivity.TAG, "Weather key is " + Singleton.getInstance().getWeatherKey());
        String exclude = "current,minutely,daily,alerts";
        String units = "metric";
        Log.i(MainActivity.TAG, "Request URL: " + getTodaysService.getForecast(lat, lon, exclude, units, Singleton.getInstance().getWeatherKey()).request().url());
        return getTodaysService.getForecast(lat, lon, exclude, units, Singleton.getInstance().getWeatherKey());
    }
}

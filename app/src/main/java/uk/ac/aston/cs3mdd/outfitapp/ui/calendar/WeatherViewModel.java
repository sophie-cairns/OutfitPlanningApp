package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3mdd.outfitapp.ui.home.Hourly;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<List<WeeklyWeather>> allForecasts;
    private MutableLiveData<List<Daily>> dailyWeather;
    public LiveData<List<Daily>> getDailyWeather() {
        return dailyWeather;
    }
    private MutableLiveData<List<Hourly>> hourlyWeather;
    public LiveData<List<Hourly>> getHourlyWeather() {
        return hourlyWeather;
    }

    public WeatherViewModel() {
        super();
        allForecasts = new MutableLiveData<>(new ArrayList<>());
        dailyWeather = new MutableLiveData<>(new ArrayList<>());
        hourlyWeather = new MutableLiveData<>(new ArrayList<>());

    }


    public void requestForecast(WeeklyWeatherRepo weeklyWeatherRepository, LatLng latLng) {
            Call<WeeklyWeather> weatherCall = weeklyWeatherRepository.getWeekly(latLng.latitude, latLng.longitude);
            Log.i("SC", "LATLNG: " + latLng.latitude + ", " + latLng.longitude);
            weatherCall.enqueue(new Callback<WeeklyWeather>() {
                @Override
                public void onResponse(Call<WeeklyWeather> call, Response<WeeklyWeather> response) {
                    if (response.isSuccessful()) {
                        WeeklyWeather weeklyWeather = response.body();
                        if (weeklyWeather != null) {
                            Log.i("SC", "Response Body: " + new Gson().toJson(weeklyWeather));
                            addWeeklyForecast(weeklyWeather);
                        } else {
                            Log.i("SC", "Response body is null");
                        }
                    }
                }

                @Override
                public void onFailure(Call<WeeklyWeather> call, Throwable t) {
                    Log.i("SC", "Error: " + t.toString());
                }
            });

    }

    public void addWeeklyForecast(WeeklyWeather weeklyWeather) {
        List<WeeklyWeather> currentList = allForecasts.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        if (weeklyWeather != null) {
            currentList.add(weeklyWeather);
            setDaily(weeklyWeather);
            setHourly(weeklyWeather);
        }

        allForecasts.setValue(currentList);

    }


    public void setDaily(WeeklyWeather weeklyWeather) {
        List<Daily> dailyWeather = weeklyWeather.getDailyList();
        this.dailyWeather.setValue(dailyWeather);

    }

    public void setHourly(WeeklyWeather weeklyWeather) {
        List<Hourly> hourlyWeather = weeklyWeather.getHourlyList();
        List<Hourly> today = new ArrayList<>();
        for (Hourly hourly : hourlyWeather) {
            LocalDate dt = hourly.getLocaleDate();
            if (dt.equals(LocalDate.now())) {
                today.add(hourly);
            }
        }
        this.hourlyWeather.setValue(today);
        Log.i("SC", "set Hourly");
    }
}

package uk.ac.aston.cs3ip.outfitapp.ui.calendar;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.ac.aston.cs3ip.outfitapp.ui.home.Hourly;

public class WeeklyWeather {

    @SerializedName("daily")
    private List<Daily> dailyList;
    @SerializedName("hourly")
    private List<Hourly> hourlyList;

    public List<Daily> getDailyList() {
        return dailyList;
    }

    public List<Hourly> getHourlyList() { return hourlyList; }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (dailyList != null) {
            builder.append("Daily Forecasts:\n");
            for (Daily dailyForecast : dailyList) {
                builder.append(dailyForecast.toString()).append("\n");
            }
        }
        if (hourlyList != null) {
            builder.append("Hourly Forecasts:\n");
            for (Hourly hourlyForecast : hourlyList) {
                builder.append(hourlyForecast.toString()).append("\n");
            }
        }

        return builder.toString();
    }
}

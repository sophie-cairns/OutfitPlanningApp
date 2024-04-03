package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Forecast {

    @SerializedName("daily")
    private List<Daily> dailyList;


    public List<Daily> getDailyList() {
        return dailyList;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (dailyList != null) {
            builder.append("Daily Forecasts:\n");
            for (Daily dailyForecast : dailyList) {
                builder.append(dailyForecast.toString()).append("\n");
            }
        }

        return builder.toString();
    }
}
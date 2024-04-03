package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class Daily implements Serializable {
    @SerializedName("dt")
    private long date;

    @SerializedName("temp")
    private Temperature temperature;

    @SerializedName("weather")
    private List<Weather> weatherList;

    @SerializedName("pop")
    private double pop;


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


    public LocalDate getLocaleDate() {
        long timestamp = date * 1000L;
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
    public String getFormattedTemperature() {
        int minTemperature = (int) Math.round(temperature.getMin());
        int maxTemperature = (int) Math.round(temperature.getMax());
        return "Min: " + minTemperature + "°C, Max: " + maxTemperature + "°C";
    }
    public String getWeatherIconUrl() {
        if (weatherList != null && !weatherList.isEmpty()) {
            Weather weatherData = weatherList.get(0);
            return getWeatherIconUrl(weatherData.getIcon());
        }
        return null;
    }
    public String getFormattedPop() {
        int cop = (int)Math.round(pop*100);
        return "Chance of Precipitation: " + cop + "%";
}
    private String getWeatherIconUrl(String iconCode) {
        return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Date/Time: ").append(date).append("\n");


        if (temperature != null) {
            builder.append("Temperature:\n").append(temperature.toString()).append("\n");
        }

        if (weatherList != null) {
            builder.append("Weather:\n");
            for (Weather w : weatherList) {
                builder.append(w.toString()).append("\n");
            }
        }
        builder.append("Probability of Precipitation: ").append(pop).append("\n");

        return builder.toString();
    }
}


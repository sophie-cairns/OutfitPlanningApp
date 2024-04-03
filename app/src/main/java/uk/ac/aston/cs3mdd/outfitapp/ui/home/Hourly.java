package uk.ac.aston.cs3mdd.outfitapp.ui.home;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Temperature;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Weather;

public class Hourly {
    @SerializedName("dt")
    private long date;
    @SerializedName("temp")
    private double temperature;
    @SerializedName("weather")
    private List<Weather> weatherList;
    @SerializedName("pop")
    private double pop;

    public long getDate() {
        return date;
    }

    public LocalDate getLocaleDate() {
        long timestamp = date * 1000L;
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        long timestamp = date * 1000L;
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTime.format(formatter);
    }


    public String getFormattedTemperature() {
        int roundedTemperature = (int) Math.round(temperature);
        return roundedTemperature + "°C ";
    }
    public String getFormattedPop() {
        int cop = (int)Math.round(pop*100);
        return "  " + cop + "%";
    }

    public String getWeatherIconUrl() {
        if (weatherList != null && !weatherList.isEmpty()) {
            Weather weatherData = weatherList.get(0);
            return getWeatherIconUrl(weatherData.getIcon());
        }
        return null;
    }

    private String getWeatherIconUrl(String iconCode) {
        return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Date/Time: ").append(date).append("\n");

        builder.append("Temperature:\n").append(temperature).append("\n");


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

package uk.ac.aston.cs3mdd.outfitapp;

import android.content.Context;


public class Singleton {
    private static Singleton _INSTANCE;
    private Context context;
    private String weatherKey;
    private String placesKey;

    private Singleton() {
        this.context = MyApplication.getAppContext();
    }

    public static Singleton getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new Singleton();
        }
        return _INSTANCE;
    }

    public String getWeatherKey() {
        return weatherKey;
    }

    public void setWeatherKey(String weatherKey) {
        this.weatherKey = weatherKey;
    }

    public String getPlacesKey() {
        return placesKey;
    }

    public void setPlacesKey(String placesKey) {
        this.placesKey = placesKey;
    }
}

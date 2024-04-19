package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.net.Uri;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClothingItemConverters {
    @TypeConverter
    public static Uri uriFromString(String value) {
        return value == null ? null : Uri.parse(value);
    }
    @TypeConverter
    public static String uriToString(Uri uri) {
        return uri == null ? null : uri.toString();
    }

    @TypeConverter
    public static List<String> listFromString(String value) {
        return value == null ? null : new Gson().fromJson(value, new TypeToken<List<String>>() {}.getType());
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        return list == null ? null : new Gson().toJson(list);
    }
}

package uk.ac.aston.cs3ip.outfitapp.ui.calendar;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventConverters {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value, formatter);
    }

    @TypeConverter
    public static String dateToString(LocalDate date) {
        return date == null ? null : date.format(formatter);
    }

}

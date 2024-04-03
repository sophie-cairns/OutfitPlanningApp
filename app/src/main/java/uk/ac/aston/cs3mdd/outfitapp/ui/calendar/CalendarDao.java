package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM event WHERE date = :date")
    LiveData<List<Event>> getEventsForDate(LocalDate date);
}

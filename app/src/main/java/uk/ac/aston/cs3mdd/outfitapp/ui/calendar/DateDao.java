package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.LocalDate;
@Dao
public interface DateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Date date);

    @Delete
    void delete(Date date);

    @Query("SELECT * FROM date WHERE date = :dateValue")
    Date getDateByValue(LocalDate dateValue);
}

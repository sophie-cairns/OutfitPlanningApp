package uk.ac.aston.cs3ip.outfitapp.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

import uk.ac.aston.cs3ip.outfitapp.ui.clothing.Outfit;
@Dao
public interface DateOutfitJoinDao {
    @Insert
    void insert(DateOutfitJoin join);

    @Query("SELECT outfits.* FROM outfits INNER JOIN date_outfit_join ON " +
            "outfits.outfitId = date_outfit_join.outfitId WHERE " +
            "date_outfit_join.date = :date")
    LiveData<List<Outfit>> getOutfitsForDate(LocalDate date);

    @Query("DELETE FROM date_outfit_join WHERE date = :date")
    void deleteOutfitsForDate(LocalDate date);

}

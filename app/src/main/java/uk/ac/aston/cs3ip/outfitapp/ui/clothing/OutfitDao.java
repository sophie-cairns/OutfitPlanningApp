package uk.ac.aston.cs3ip.outfitapp.ui.clothing;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OutfitDao {
    @Insert
    long insert(Outfit outfit);

    @Update
    void update(Outfit outfit);

    @Delete
    void delete(Outfit outfit);

    @Query("DELETE FROM outfits WHERE outfitId = :outfitId")
    void deleteOutfitById(long outfitId);

    @Query("SELECT * FROM outfits")
    LiveData<List<Outfit>> getAllOutfits();

    @Query("SELECT outfitId FROM outfits ORDER BY outfitId DESC LIMIT 1")
    LiveData<Long> getLastInsertedOutfitId();

    @Transaction
    @Query("SELECT * FROM outfits")
    LiveData<List<OutfitWithClothingItems>> getOutfitsWithClothingItems();

    @Transaction
    @Query("SELECT * FROM outfits WHERE outfitId = :outfitId")
    LiveData<OutfitWithClothingItems> getOutfitWithClothingItems(long outfitId);

}

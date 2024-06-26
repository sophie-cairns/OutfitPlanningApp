package uk.ac.aston.cs3ip.outfitapp.ui.clothing;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;


@Dao
public interface ClothingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClothingItem clothingItem);

    @Transaction
    @Delete
    void delete(ClothingItem clothingItem);

    @Query("SELECT * FROM clothingitem")
    LiveData<List<ClothingItem>> getAllClothingItems();

    @Query("SELECT DISTINCT brand FROM clothingitem")
    List<String> getBrands();
    @Query("SELECT tags FROM clothingitem WHERE clothingId = :id")
    String getTagsByClothingItem(Long id);
}

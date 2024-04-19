package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
@Dao
public interface OutfitClothingJoinDao {
    @Insert
    void insert(OutfitClothingItemJoin join);

    @Query("SELECT clothingitem.* FROM clothingitem INNER JOIN outfit_clothing_item_join ON " +
            "clothingitem.clothingId = outfit_clothing_item_join.clothingId WHERE " +
            "outfit_clothing_item_join.outfitId = :outfitId")
    LiveData<List<ClothingItem>> getClothingItemsForOutfit(long outfitId);

    @Query("SELECT outfitId FROM outfit_clothing_item_join WHERE clothingId = :clothingId")
    List<Long> getOutfitsByClothingId(long clothingId);

    @Query("SELECT COUNT(*) FROM outfit_clothing_item_join WHERE outfitId = :outfitId")
    int getClothingOutfitJoinCount(long outfitId);

    @Query("DELETE FROM outfit_clothing_item_join WHERE outfitId = :outfitId")
    void deleteClothingItemsForOutfit(long outfitId);
}

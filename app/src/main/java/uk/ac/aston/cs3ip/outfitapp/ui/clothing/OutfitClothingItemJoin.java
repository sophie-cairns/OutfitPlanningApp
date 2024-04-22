package uk.ac.aston.cs3ip.outfitapp.ui.clothing;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "outfit_clothing_item_join",
        primaryKeys = { "outfitId", "clothingId" },
        foreignKeys = {
                @ForeignKey(entity = Outfit.class,
                        parentColumns = "outfitId",
                        childColumns = "outfitId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ClothingItem.class,
                        parentColumns = "clothingId",
                        childColumns = "clothingId",
                        onDelete = ForeignKey.CASCADE)
        })
public class OutfitClothingItemJoin {
    @ColumnInfo(index = true)
    public long outfitId;
    @ColumnInfo(index = true)
    public long clothingId;

    public OutfitClothingItemJoin(long outfitId, long clothingId) {
        this.outfitId = outfitId;
        this.clothingId = clothingId;
    }

    public long getOutfitId() {
        return outfitId;
    }

    public void setOutfitId(long outfitId) {
        this.outfitId = outfitId;
    }

    public long getClothingId() {
        return clothingId;
    }

    public void setClothingItemId(long clothingId) {
        this.clothingId = clothingId;
    }
}

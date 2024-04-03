package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class OutfitWithClothingItems {
    @Embedded
    public Outfit outfit;
    @Relation(
            parentColumn = "outfitId",
            entityColumn = "clothingId",
            associateBy = @Junction(OutfitClothingItemJoin.class)
    )
    public List<ClothingItem> clothingItems;

    public OutfitWithClothingItems() {
    }

    public Outfit getOutfit() {
        return outfit;
    }

    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }

    public List<ClothingItem> getClothingItems() {
        return clothingItems;
    }

    public void setClothingItems(List<ClothingItem> clothingItems) {
        this.clothingItems = clothingItems;
    }
}

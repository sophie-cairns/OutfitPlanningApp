package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.lifecycle.ViewModel;

import java.util.List;

public class OutfitViewModel extends ViewModel {
    private OutfitDao outfitDao;
    private ClothingDao clothingDao;
    private OutfitClothingJoinDao joinDao;
    public OutfitViewModel(OutfitDao outfitDao, ClothingDao clothingDao, OutfitClothingJoinDao joinDao) {
        this.outfitDao = outfitDao;
        this.clothingDao = clothingDao;
        this.joinDao = joinDao;
    }

    public void addOutfitWithClothingItems(Outfit outfit, List<ClothingItem> clothingItems) {
        long outfitId = outfit.getId();
        for (ClothingItem clothingItem : clothingItems) {
            long clothingItemId = clothingItem.getId();
            OutfitClothingItemJoin join = new OutfitClothingItemJoin(outfitId, clothingItemId);
            // Insert the join into the association table
            joinDao.insert(join);
        }
    }
}

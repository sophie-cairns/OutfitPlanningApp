package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ClothingViewModel extends ViewModel {

    private MutableLiveData<Boolean> isClothingView = new MutableLiveData<>(true);
    private final MutableLiveData<List<List<String>>> selectedFilters = new MutableLiveData<>();
    public LiveData<Boolean> getIsClothingView() {
        return isClothingView;
    }

    public void setIsClothingView(boolean isClothing) {
        isClothingView.setValue(isClothing);
    }

    public LiveData<List<List<String>>> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(List<List<String>> filters) {
        selectedFilters.setValue(filters);
    }
}
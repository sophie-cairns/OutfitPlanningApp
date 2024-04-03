package uk.ac.aston.cs3mdd.outfitapp.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {
    private MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    public void setCurrentLocation(Location location) {
        currentLocation.setValue(location);
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }
}

package uk.ac.aston.cs3ip.outfitapp.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class LocationViewModel extends ViewModel {
    private MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    public void setCurrentLocation(Location location) {
        currentLocation.setValue(location);
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LatLng givenLocation;

    public LatLng getGivenLocation() {
        return givenLocation;
    }

    public void setGivenLocation(LatLng givenLocation) {
        this.givenLocation = givenLocation;
    }
}

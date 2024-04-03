package uk.ac.aston.cs3mdd.outfitapp.ui.home;

import static uk.ac.aston.cs3mdd.outfitapp.ui.calendar.CalendarFragment.getAddressFromLatLng;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.Singleton;
import uk.ac.aston.cs3mdd.outfitapp.databinding.FragmentHomeBinding;
import uk.ac.aston.cs3mdd.outfitapp.ui.LocationViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Event;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.EventAdapter;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.GetWeeklyWeather;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.WeatherViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.WeeklyWeatherRepo;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingDbViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.Outfit;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.OutfitAdapter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ClothingDbViewModel clothingDbViewModel;
    private WeatherViewModel weatherViewModel;
    private LocationViewModel locationViewModel;
    private OutfitAdapter outfitAdapter;
    private EventAdapter eventAdapter;
    private TodaysWeatherAdapter weatherAdapter;
    private LatLng latLng;
    private LatLng newLatLng;
    private Button changeLocationButton;
    private Observer<Location> locationObserver;
    private GetWeeklyWeather weatherService;
    private TextView locationTextView;
    private AutocompleteSupportFragment autocompleteFragment;
    private View changeLocationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        Places.initialize(getContext(), Singleton.getInstance().getPlacesKey());

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = view.findViewById(R.id.text_date);
        locationTextView = view.findViewById(R.id.text_location);


        changeLocationButton = view.findViewById((R.id.button_change_location));

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);
        weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        LocalDate today = LocalDate.now();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String daySuffix;
        if (day >= 11 && day <= 13) {
            daySuffix = "th";
        } else {
            switch (day % 10) {
                case 1:
                    daySuffix = "st";
                    break;
                case 2:
                    daySuffix = "nd";
                    break;
                case 3:
                    daySuffix = "rd";
                    break;
                default:
                    daySuffix = "th";
                    break;
            }
        }

        formattedDate += daySuffix;

        SimpleDateFormat monthFormat = new SimpleDateFormat(" MMMM", Locale.getDefault());
        formattedDate += monthFormat.format(calendar.getTime());

        textView.setText(formattedDate);

        final Observer<List<Outfit>> outfitListObserver = new Observer<List<Outfit>>() {
            @Override
            public void onChanged(@Nullable final List<Outfit> outfitList) {
                requireActivity().runOnUiThread(() -> {
                    if (outfitAdapter != null) {
                        Log.i("SC", " DATE onChanged: mAdapter is NOT null");
                        outfitAdapter.updateData(outfitList);
                    } else {
                        Log.i("SC", "onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getOutfitsForDate(today).observe(getViewLifecycleOwner(), outfitListObserver);
        RecyclerView outfitRecyclerView = view.findViewById(R.id.outfitRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        outfitAdapter = new OutfitAdapter(getContext(), new ArrayList<>(), clothingDbViewModel, false);
        outfitRecyclerView.setLayoutManager(layoutManager);
        outfitRecyclerView.setAdapter(outfitAdapter);

        final Observer<List<Event>> eventListObserver = new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable final List<Event> eventList) {
                requireActivity().runOnUiThread(() -> {
                    if (eventAdapter != null) {
                        Log.i("SC", " DATEEVENT onChanged: mAdapter is NOT null");
                        eventAdapter.updateData(eventList);
                    } else {
                        Log.i("SC", " DATEEVENT onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getEventsForDate(today).observe(getViewLifecycleOwner(), eventListObserver);
        RecyclerView eventRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventAdapter = new EventAdapter(getContext(), new ArrayList<>(), clothingDbViewModel);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventRecyclerView.setAdapter(eventAdapter);

        observeLocation();

        final Observer<List<Hourly>> weatherObserver = new Observer<List<Hourly>>() {
            @Override
            public void onChanged(@Nullable List<Hourly> hourlyWeather) {
                weatherAdapter.updateData(hourlyWeather);
                Log.i("SC", "Hourly list size: " + hourlyWeather.size());

            }
        };
        weatherViewModel.getHourlyWeather().observe(getViewLifecycleOwner(), weatherObserver);
        RecyclerView weatherRecyclerView = view.findViewById(R.id.weatherRecyclerView);
        LinearLayoutManager weatherLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        weatherAdapter = new TodaysWeatherAdapter(getContext(), new ArrayList<>(), weatherViewModel);
        weatherRecyclerView.setLayoutManager(weatherLayoutManager);
        weatherRecyclerView.setAdapter(weatherAdapter);

        changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLocationPopupMenu();
            }
        });

        return view;
    }

    private void showChangeLocationPopupMenu() {
        if (changeLocationView == null) {
            changeLocationView = getLayoutInflater().inflate(R.layout.location_view, null);
            if (autocompleteFragment == null) {
                autocompleteFragment = (AutocompleteSupportFragment)
                        getChildFragmentManager().findFragmentById(R.id.autocompleteFragment);
                if (autocompleteFragment == null) {
                    autocompleteFragment = AutocompleteSupportFragment.newInstance();
                    getChildFragmentManager().beginTransaction()
                            .add(R.id.autocompleteFragment, autocompleteFragment)
                            .commit();
                }
            }
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

                @Override
                public void onPlaceSelected(@NonNull Place place) {

                    newLatLng = place.getLatLng();


                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e("SC", "onError: " + status);
                }
            });
        }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (changeLocationView.getParent() != null) {
            ((ViewGroup) changeLocationView.getParent()).removeView(changeLocationView);
        }
            builder.setView(changeLocationView);


            Button confirmButton = changeLocationView.findViewById(R.id.confirmButton);
            Button currentLocationButton = changeLocationView.findViewById(R.id.currentLocationButton);


            currentLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newLatLng = null;
                }
            });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newLatLng != null) {
                    locationTextView.setText(getAddressFromLatLng(requireContext(), newLatLng));
                    locationViewModel.getCurrentLocation().removeObserver(locationObserver);
                    weatherViewModel.requestForecast(new WeeklyWeatherRepo(weatherService), newLatLng);
                } else {
                    observeLocation();
                }

                alertDialog.dismiss();
            }
        });
    }

    public void observeLocation() {
        locationObserver = new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    latLng = new LatLng(latitude, longitude);
                    Log.i("SC", "non default");
                    Log.i("SC", "Lat" + location.getLatitude());
                    locationTextView.setText(getAddressFromLatLng(requireContext(),latLng));
                    Retrofit weatherretrofit = new Retrofit.Builder()
                            .baseUrl("https://api.openweathermap.org/data/3.0/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    weatherService = weatherretrofit.create(GetWeeklyWeather.class);
                    weatherViewModel.requestForecast(new WeeklyWeatherRepo(weatherService), latLng);


                }

            }
        };
        locationViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), locationObserver);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
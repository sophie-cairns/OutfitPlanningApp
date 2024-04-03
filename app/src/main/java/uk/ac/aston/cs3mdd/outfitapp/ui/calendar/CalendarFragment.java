package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import static com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.outfitapp.MyApplication;
import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.databinding.FragmentCalendarBinding;
import uk.ac.aston.cs3mdd.outfitapp.ui.LocationViewModel;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnDateClickListener {

    private FragmentCalendarBinding binding;
    private LocationViewModel locationViewModel;
    private TextView locationTextView;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LinearLayout calendarContentLayout;
    private LocalDate selectedDate;
    private List<Daily> weatherList;
    private WeatherViewModel weatherViewModel;
    private LatLng latLng;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        calendarContentLayout = view.findViewById(R.id.calendar_content_layout);
        locationTextView = view.findViewById(R.id.text_location);

        weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        selectedDate = LocalDate.now();

        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, selectedDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MyApplication.getAppContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);


        Button previousMonthButton = view.findViewById(R.id.previousMonthButton);
        Button nextMonthButton = view.findViewById(R.id.nextMonthButton);


        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction();
            }
        });

        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction();
            }
        });


            latLng = new LatLng(40, 50);


        final Observer<Location> locationObserver = new Observer<Location>() {
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

                    GetWeeklyWeather weatherService = weatherretrofit.create(GetWeeklyWeather.class);
                    weatherViewModel.requestForecast(new WeeklyWeatherRepo(weatherService), latLng);


                }

            }
        };
        locationViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), locationObserver);


        final Observer<List<Daily>> weatherObserver = new Observer<List<Daily>>() {
            @Override
            public void onChanged(@Nullable List<Daily> todayWeather) {
                weatherList = todayWeather;
                Log.i("SC", "Weather list size: " + todayWeather.size());

            }
        };
        weatherViewModel.getDailyWeather().observe(getViewLifecycleOwner(), weatherObserver);


        return view;
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, selectedDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MyApplication.getAppContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }
    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }


    public void previousMonthAction()
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction()
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onDateClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
            selectedDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), Integer.parseInt(dayText));
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show(); //may delete

            Daily selectedWeather = null;
            if (weatherList != null) {
                Log.i("SC", "Weather list not null");
                for (Daily weather : weatherList) {
                    if (weather.getLocaleDate().isEqual(selectedDate)) {
                        selectedWeather = weather;
                        break;
                    }
                }
            }
//            Log.i("SC", selectedWeather.toString());
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate.toString());
            bundle.putSerializable("weather", selectedWeather);

            DateFragment dateFragment = new DateFragment();
            dateFragment.setArguments(bundle);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, dateFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            calendarContentLayout.setVisibility(View.GONE);

        }
    }

    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String throughfare = address.getThoroughfare();
                Log.i("Sc", "Line 1: " + throughfare);
                String city = address.getLocality();
                Log.i("SC", "city: " + city);
                String postCode = address.getPostalCode();
                Log.i("SC", "postCode: " + postCode);
                String addressLine = address.getAddressLine(0);
                Log.i("SC", "address: " + addressLine);
                String[] parts = addressLine.split(",");
                String add = parts[1];
                int index = add.indexOf(postCode);
                if (index != -1) {
                    return add.substring(0, index).trim();
                } else if (city != null) {
                    return city;
                } else if (throughfare != null) {
                    return throughfare;
                } else {
                    return "location error";
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
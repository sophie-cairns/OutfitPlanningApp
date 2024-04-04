package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.databinding.FragmentDateBinding;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingDbViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.Outfit;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.OutfitAdapter;


public class DateFragment extends Fragment {

    private FragmentDateBinding binding;
    private Button backButton;
    private Button selectOutfitButton;
    private Button addEventButton;
    private TextView dateTextView;
    private TextView temperatureTextView;
    private TextView chanceOfPrecipitationTextView;
    private ImageView iconImageView;
    private LocalDate selectedDate;
    private ClothingDbViewModel clothingDbViewModel;
    private OutfitAdapter outfitAdapter;
    private EventAdapter eventAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_date, container, false);

        dateTextView = view.findViewById(R.id.textDate);
        temperatureTextView = view.findViewById(R.id.text_view_temperature);
        chanceOfPrecipitationTextView = view.findViewById(R.id.text_view_chance_of_precipitation);
        iconImageView = view.findViewById(R.id.image_view_weather_icon);

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);


        String selectedDateStr = getArguments().getString("selectedDate");
        Daily weather = (Daily) getArguments().getSerializable("weather");
        selectedDate = LocalDate.parse(selectedDateStr);

        if (selectedDate != null) {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE d", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate);


            int day = selectedDate.getDayOfMonth();
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

            DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern(" MMMM", Locale.getDefault());
            formattedDate += monthFormat.format(selectedDate);

            // Set the formatted date to TextView
            dateTextView.setText(formattedDate);
        }

        if (weather != null) {
            temperatureTextView.setText(weather.getFormattedTemperature());
            chanceOfPrecipitationTextView.setText(weather.getFormattedPop());
            String iconUrl = weather.getWeatherIconUrl();
            if (iconUrl != null) {
                Glide.with(requireContext())
                        .load(iconUrl)
                        .into(iconImageView);
            }
        } else {
            temperatureTextView.setText("Weather N/A");
            chanceOfPrecipitationTextView.setText("");
        }

        final Observer<List<Outfit>> outfitListObserver = new Observer<List<Outfit>>() {
            @Override
            public void onChanged(@Nullable final List<Outfit> outfitList) {
                requireActivity().runOnUiThread(() -> {
                    if (outfitAdapter != null) {
                        Log.i("SC", " DATEOUTFIT onChanged: mAdapter is NOT null");
                        outfitAdapter.updateData(outfitList);
                    } else {
                        Log.i("SC", " DATEOUTFIT onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getOutfitsForDate(selectedDate).observe(getViewLifecycleOwner(), outfitListObserver);
        RecyclerView outfitRecyclerView = view.findViewById(R.id.outfitRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        outfitAdapter = new OutfitAdapter(getContext(), new ArrayList<>(), clothingDbViewModel, false);
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
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
        clothingDbViewModel.getEventsForDate(selectedDate).observe(getViewLifecycleOwner(), eventListObserver);
        RecyclerView eventRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventAdapter = new EventAdapter(getContext(), new ArrayList<>(), clothingDbViewModel);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventRecyclerView.setAdapter(eventAdapter);


        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarContents();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        selectOutfitButton = view.findViewById(R.id.select_outfits_button);
        selectOutfitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSelectOutfitFragment();
            }
        });

        addEventButton = view.findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddEventFragment();
            }
        });

        return view;
    }

    private void showCalendarContents() {
        View calendarContent = requireActivity().findViewById(R.id.calendar_content_layout);
        if (calendarContent != null) {
            calendarContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigateToSelectOutfitFragment() {
        NavController navController = Navigation.findNavController(requireView());
        Bundle args = new Bundle();
        args.putSerializable("selectedDate", selectedDate);
        args.putSerializable("home", false);
        navController.navigate(R.id.action_navigation_calendar_to_navigation_select_outfit, args);
    }

    private void navigateToAddEventFragment() {
        NavController navController = Navigation.findNavController(requireView());
        Bundle args = new Bundle();
        args.putSerializable("selectedDate", selectedDate);
        args.putSerializable("home", false);
        navController.navigate(R.id.action_navigation_calendar_to_navigation_add_event);
    }

}
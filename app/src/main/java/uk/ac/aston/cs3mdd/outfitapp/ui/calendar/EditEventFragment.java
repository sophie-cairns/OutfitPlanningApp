package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingDbViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingItem;

public class EditEventFragment extends Fragment {
    private Button confirmButton;
    private Button cancelButton;
    private Event event;
    private ClothingDbViewModel clothingDbViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }

        confirmButton = view.findViewById(R.id.confirmButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCalendarFragment();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) view.findViewById(R.id.editTextTitle)).getText().toString();
                String location = ((EditText) view.findViewById(R.id.editTextLocation)).getText().toString();
                LocalDate date = parseDateInput(((EditText) view.findViewById(R.id.editTextDate)).getText().toString());
                String time = ((EditText) view.findViewById(R.id.editTextTime)).getText().toString();

                Date existingDate;
                existingDate = clothingDbViewModel.getDateByLocalDate(date);
                Log.i("SC", "existingDate" + existingDate);

                if (existingDate == null) {
                    Log.i("SC", "date is null");
                    Date newDate = new Date();
                    newDate.setDate(date);
                    clothingDbViewModel.insertDate(newDate);
                }

                Event event = new Event();
                event.event = title;
                event.location = location;
                event.date = date;
                event.time = time;

                clothingDbViewModel.insertEvent(event);
                navigateToCalendarFragment();
            }
        });


        return view;
    }

    private void navigateToCalendarFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_edit_event_to_navigation_calendar);
    }

    private LocalDate parseDateInput(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateString, formatter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

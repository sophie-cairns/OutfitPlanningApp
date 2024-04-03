package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingDbViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingItem;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.Outfit;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.OutfitAdapter;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.OutfitClothingItemJoin;

public class SelectOutfitFragment extends Fragment {
    private FrameLayout frameLayout;
    private View outfitsViewLayout;
    private Button confirmButton;
    private ClothingDbViewModel clothingDbViewModel;
    private RecyclerView outfitRecyclerView;
    private OutfitAdapter outfitAdapter;
    private List<Outfit> list = new ArrayList<>();
    private List<Outfit> selectedOutfits = new ArrayList<>();
    private LocalDate selectedDate;
    private List<Long> selectedIds;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_outfit, container, false);

        Bundle args = getArguments();
        if (args != null) {
            Log.i("SC", "args not null");
            selectedDate = (LocalDate) args.getSerializable("selectedDate");
        }

        outfitsViewLayout = inflater.inflate(R.layout.outfits_view, null);

        frameLayout = view.findViewById(R.id.frame_container);
        frameLayout.addView(outfitsViewLayout);

        confirmButton = view.findViewById(R.id.confirm_button);

        Button addButton = outfitsViewLayout.findViewById(R.id.add_button);
        addButton.setVisibility(View.GONE);

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOutfits();
                navigateToDateFragment();
            }
        });


        final Observer<List<Outfit>> outfitListObserver = new Observer<List<Outfit>>() {
            @Override
            public void onChanged(@Nullable final List<Outfit> outfitList) {
                Log.i("SC", "onChanged: Updating UI with new data");
                requireActivity().runOnUiThread(() -> {
                    if (outfitAdapter != null) {
                        list = outfitList;
                        outfitAdapter.updateData(outfitList);
                        Log.i("SC", "onChanged: Success");
                    } else {
                        Log.i("SC", "onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getAllOutfits().observe(getViewLifecycleOwner(), outfitListObserver);
        outfitRecyclerView = outfitsViewLayout.findViewById(R.id.outfitRecyclerView);
        outfitAdapter = new OutfitAdapter(getContext(), new ArrayList<>(), clothingDbViewModel, true);
        outfitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        outfitRecyclerView.setAdapter(outfitAdapter);

        outfitAdapter.setOnItemClickListener(new OutfitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("SC", "OnItemClick Outfit");
                Outfit clickedItem = list.get(position);
                Log.i("SC", "clicked item id: " + clickedItem.getId());
                if (selectedIds.contains(clickedItem.getId())) {
                    Log.i("SC", "Removing ");
                    selectedIds.remove(clickedItem.getId());
                } else {
                    Log.i("SC", "adding ");
                    selectedIds.add(clickedItem.getId());
                }

            }
        });

        clothingDbViewModel.getOutfitsForDate(selectedDate).observe(getViewLifecycleOwner(), new Observer<List<Outfit>>() {
            @Override
            public void onChanged(List<Outfit> outfits) {
                selectedOutfits = outfits;
                Log.i("SC", "selected outfits set");
                selectedIds = new ArrayList<>();
                for (Outfit item : selectedOutfits) {
                    selectedIds.add(item.getId());
                }
                if (outfitAdapter != null) {
                    outfitAdapter.setSelected(selectedIds, outfitRecyclerView);
                    Log.i("SC", "selected Clothing items sent to ADAPTER");

                }
            }
        });

        return view;
    }

    private void navigateToDateFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_select_outfit_to_navigation_calendar);
    }

    private void selectOutfits() {

        if (!selectedIds.isEmpty()) {
            Date existingDate;
            existingDate = clothingDbViewModel.getDateByLocalDate(selectedDate);
            Log.i("SC", "existingDate" + existingDate);

            List<DateOutfitJoin> dateOutfitJoins = new ArrayList<>();

            if (existingDate == null) {
                Log.i("SC", "date is null");
                Date newDate = new Date();
                newDate.setDate(selectedDate);
                clothingDbViewModel.insertDate(newDate);
            } else {
                selectedDate = existingDate.getDate();
            }
            clothingDbViewModel.deleteOutfitsForDate(selectedDate);
            for (Long outfitId : selectedIds) {
                Log.i("SC", "Outfit ID" + outfitId);
                DateOutfitJoin join = new DateOutfitJoin(selectedDate, outfitId);
                dateOutfitJoins.add(join);
            }

            clothingDbViewModel.insertDateOutfitJoins(dateOutfitJoins);
            selectedIds.clear();
        } else {
            Log.i("SC", "Selected outfits empty");
        }
    }

}

package uk.ac.aston.cs3ip.outfitapp.ui.clothing;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class EditOutfitFragment extends Fragment {

    private Button confirmButton;
    private ClothingViewModel viewModel;
    private FrameLayout frameLayout;
    private ClothingDbViewModel clothingDbViewModel;
    private View clothingViewLayout;
    private ClothingAdapter clothingAdapter;
    private RecyclerView recyclerView;
    private List<ClothingItem> list = new ArrayList<>();
    private List<ClothingItem> selectedClothingItems = new ArrayList<>();
    private Outfit outfit;
    private Long outfitId;
    private List<Long> selectedIds;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_outfit, container, false);

        viewModel = new ViewModelProvider(this).get(ClothingViewModel.class);

        confirmButton = view.findViewById(R.id.confirm_button);

        clothingViewLayout = inflater.inflate(R.layout.clothing_view, null);
        frameLayout = view.findViewById(R.id.frame_container);
        frameLayout.addView(clothingViewLayout);

        Button addButton = clothingViewLayout.findViewById(R.id.add_button);
        addButton.setVisibility(View.GONE);

        if (getArguments() != null) {
            Log.i("SC", "BUNDLE NOT NULL EDITOUTFIT");
            outfit = (Outfit) getArguments().getSerializable("outfit");
        }

        outfitId = outfit.getId();

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOutfit(outfit);
                navigateToClothingFragment();
            }
        });

        final Observer<List<ClothingItem>> clothingListObserver = new Observer<List<ClothingItem>>() {
            @Override
            public void onChanged(@Nullable final List<ClothingItem> clothingList) {
                Log.i("SC", "onChanged: Updating UI with new data");
                requireActivity().runOnUiThread(() -> {
                    if (clothingAdapter != null) {
                        list = clothingList;
                        clothingAdapter.updateData(clothingList);
                        Log.i("SC", "onChanged: Success");
                    } else {
                        Log.i("SC", "onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getAllClothingItems().observe(getViewLifecycleOwner(), clothingListObserver);
        recyclerView = clothingViewLayout.findViewById(R.id.clothingRecyclerView);
        clothingAdapter = new ClothingAdapter(getContext(), new ArrayList<>(), clothingDbViewModel, true, false);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(clothingAdapter);

        clothingAdapter.setOnItemClickListener(new ClothingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("SC", "OnItemClick Clothing Fragment");
                ClothingItem clickedItem = list.get(position);
                if (selectedIds.contains(clickedItem.getId())) {
                    Log.i("SC", "contains");
                    selectedIds.remove(clickedItem.getId());
                } else {
                    Log.i("SC", "does not");
                    selectedIds.add(clickedItem.getId());
                }

            }
        });

        clothingDbViewModel.getClothingItemsForOutfit(outfitId).observe(getViewLifecycleOwner(), new Observer<List<ClothingItem>>() {
            @Override
            public void onChanged(List<ClothingItem> clothingItems) {
                selectedClothingItems = clothingItems;
                Log.i("SC", "selected Clothing items set");
                selectedIds = new ArrayList<>();
                for (ClothingItem item : selectedClothingItems) {
                    selectedIds.add(item.getId());
                }
                if (clothingAdapter != null) {
                    clothingAdapter.setSelected(selectedIds, recyclerView);
                    Log.i("SC", "selected Clothing items sent to ADAPTER");

                }
            }
        });

        return view;
    }

    private void navigateToClothingFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_edit_outfit_to_navigation_clothing);
    }

    public void saveOutfit(Outfit outfit) {
        if (!selectedIds.isEmpty()) {

            outfitId = outfit.getId();

            clothingDbViewModel.deleteClothingItemsForOutfit(outfitId);

            List<OutfitClothingItemJoin> outfitClothingItemJoins = new ArrayList<>();

            for (Long clothingId : selectedIds) {
                OutfitClothingItemJoin join = new OutfitClothingItemJoin(outfitId, clothingId);
                outfitClothingItemJoins.add(join);
            }

            clothingDbViewModel.insertOutfitClothingItemJoins(outfitClothingItemJoins);

            selectedIds.clear();

        }
    }

}

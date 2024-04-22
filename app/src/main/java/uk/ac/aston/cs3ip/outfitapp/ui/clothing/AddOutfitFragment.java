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

public class AddOutfitFragment extends Fragment {

    private Button confirmButton;
    private ClothingViewModel viewModel;
    private FrameLayout frameLayout;
    private ClothingDbViewModel clothingDbViewModel;
    private View clothingViewLayout;
    private ClothingAdapter clothingAdapter;
    private RecyclerView recyclerView;
    private List<ClothingItem> list = new ArrayList<>();
    private List<ClothingItem> selectedClothingItems = new ArrayList<>();

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
        Button filterButton = clothingViewLayout.findViewById(R.id.filter_button);
        filterButton.setVisibility(View.GONE);

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOutfit();
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
                if (selectedClothingItems.contains(clickedItem)) {
                    selectedClothingItems.remove(clickedItem);
                } else {
                    selectedClothingItems.add(clickedItem);
                }

            }
        });

        return view;
    }

    private void navigateToClothingFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_add_outfit_to_navigation_clothing);
    }

    public void saveOutfit() {
        if (!selectedClothingItems.isEmpty()) {

            Outfit outfit = new Outfit();
            Long outfitId = clothingDbViewModel.insertOutfit(outfit);

                    List<OutfitClothingItemJoin> outfitClothingItemJoins = new ArrayList<>();

                    for (ClothingItem clothingItem : selectedClothingItems) {
                        OutfitClothingItemJoin join = new OutfitClothingItemJoin(outfitId, clothingItem.getId());
                        outfitClothingItemJoins.add(join);
                    }

                    clothingDbViewModel.insertOutfitClothingItemJoins(outfitClothingItemJoins);

                    selectedClothingItems.clear();

        }
    }

}

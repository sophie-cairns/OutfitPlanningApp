package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.databinding.FragmentClothingBinding;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.DateFragment;

public class ClothingFragment extends Fragment {

    private ClothingViewModel viewModel;
    private FragmentClothingBinding binding;
    private Button addClothingButton;
    private Button addOutfitButton;
    private Button filterButton;
    private ClothingDbViewModel clothingDbViewModel;
    private ClothingAdapter clothingAdapter;
    private OutfitAdapter outfitAdapter;
    private RecyclerView recyclerView;
    private RecyclerView outfitRecyclerView;
    private TextView clothingText;
    private TextView outfitText;
    private FrameLayout frameLayout;
    private View clothingViewLayout;
    private View outfitsViewLayout;
    private List<List<String>> selectedFilters;
    private List<String> typeFilters;
    private List<String> colourFilters;
    private List<String> brandFilters;
    private List<String> tagFilters;
    private List<ClothingItem> clothingItemList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SC", "SavedInstanceStateXXX: " + savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ClothingViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clothing, container, false);

        Log.i("SC", "SavedInstanceState: " + savedInstanceState);
        clothingViewLayout = inflater.inflate(R.layout.clothing_view, null);
        outfitsViewLayout = inflater.inflate(R.layout.outfits_view, null);

        clothingText = view.findViewById(R.id.textClothing);
        outfitText = view.findViewById(R.id.textOutfits);

        frameLayout = view.findViewById(R.id.frame_container);

        viewModel.getIsClothingView().observe(getViewLifecycleOwner(), isClothing -> {
            if (isClothing) {
                setToClothing();
            } else {
                setToOutfit();
            }
        });

        addClothingButton = clothingViewLayout.findViewById(R.id.add_button);
        addOutfitButton = outfitsViewLayout.findViewById(R.id.add_button);
        filterButton = clothingViewLayout.findViewById(R.id.filter_button);

        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);

        clothingItemList = new ArrayList<>();

        clothingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setIsClothingView(true);
            }
        });
        outfitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setIsClothingView(false);
            }
        });


        final Observer<List<ClothingItem>> clothingListObserver = new Observer<List<ClothingItem>>() {
            @Override
            public void onChanged(@Nullable final List<ClothingItem> clothingList) {
                Log.i("SC", "onChanged: Updating UI with new data");
                requireActivity().runOnUiThread(() -> {
                    if (clothingAdapter != null) {
                        clothingAdapter.updateData(clothingList);
                        clothingAdapter.applyFilters(typeFilters, colourFilters, brandFilters, tagFilters);
                        clothingItemList = clothingList;
                        Log.i("SC", "onChanged: Success");
                    } else {
                        Log.i("SC", "onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getAllClothingItems().observe(getViewLifecycleOwner(), clothingListObserver);
        recyclerView = clothingViewLayout.findViewById(R.id.clothingRecyclerView);
        clothingAdapter = new ClothingAdapter(getContext(), new ArrayList<>(), clothingDbViewModel, false, false);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(clothingAdapter);

        clothingAdapter.setOnItemClickListener(new ClothingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("SC", "OnItemClick Clothing Fragment");
                showClothingPopupMenu(position);
            }
        });

        viewModel.getSelectedFilters().observe(getViewLifecycleOwner(), selectedFilters -> {
            Log.i("SC", "Filter size: " + selectedFilters.size());
            clothingAdapter.applyFilters(typeFilters, colourFilters, brandFilters, tagFilters);
//            updateUI(selectedFilters,)
        });


        final Observer<List<Outfit>> outfitListObserver = new Observer<List<Outfit>>() {
            @Override
            public void onChanged(@Nullable final List<Outfit> outfitList) {
                requireActivity().runOnUiThread(() -> {
                    if (outfitAdapter != null) {
                        outfitAdapter.updateData(outfitList);
                    } else {
                        Log.i("SC", "onChanged: mAdapter is null");
                    }
                });
            }
        };
        clothingDbViewModel.getAllOutfits().observe(getViewLifecycleOwner(), outfitListObserver);
        outfitRecyclerView = outfitsViewLayout.findViewById(R.id.outfitRecyclerView);
        outfitAdapter = new OutfitAdapter(getContext(), new ArrayList<>(), clothingDbViewModel, false);
        GridLayoutManager outfitLayoutManager = new GridLayoutManager(getContext(), 2);
        outfitRecyclerView.setLayoutManager(outfitLayoutManager);
        outfitRecyclerView.setAdapter(outfitAdapter);

        outfitAdapter.setOnItemClickListener(new OutfitAdapter.OnItemClickListener() {
                                           @Override
                                           public void onItemClick(int position) {
                                               Log.i("SC", "OnItemClick Outfit");
                                                showOutfitPopupMenu(position);
                                           }
                                       });


        addClothingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddClothingFragment();
            }
        });

        addOutfitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddOutfitFragment();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersPopupMenu();
            }
        });

        return view;
    }



    private void navigateToAddClothingFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_clothingFragment_to_addClothingFragment);
    }

    private void navigateToEditClothingFragment(Bundle bundle) {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_clothingFragment_to_editClothingFragment, bundle);
    }

    private void navigateToAddOutfitFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_clothingFragment_to_addOutfitFragment);
    }

    private void navigateToEditOutfitFragment(Bundle bundle) {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_clothingFragment_to_editOutfitFragment, bundle);
    }

    private void setToClothing() {
        clothingText.setPaintFlags(clothingText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        outfitText.setPaintFlags(outfitText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        frameLayout.removeAllViews();
        frameLayout.addView(clothingViewLayout);
    }

    private void setToOutfit() {
        outfitText.setPaintFlags(outfitText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        clothingText.setPaintFlags(clothingText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        frameLayout.removeAllViews();
        frameLayout.addView(outfitsViewLayout);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showClothingPopupMenu(int position) {
        View itemView = recyclerView.getChildAt(position);
        ClothingItem clothingItem = clothingAdapter.getItem(position);
        if (clothingItem != null) {
            Log.i("SC", "ItemView not null");

            PopupMenu clothingPopupMenu = new PopupMenu(requireContext(), itemView);
            clothingPopupMenu.inflate(R.menu.clothing_options);
            clothingPopupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    Log.i("SC", "edit");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("clothingItem", clothingItem);
                    navigateToEditClothingFragment(bundle);
                } else if (item.getItemId() == R.id.delete) {
                    Log.i("SC", "delete");
                    clothingDbViewModel.deleteClothingItem(clothingItem);
                } else {
                    Log.i("SC", "x");

                }
                return false;
            });
            clothingPopupMenu.show();
        } else {
            Log.i("SC", "itemView null");
        }
    }

    private void showOutfitPopupMenu(int position) {
        View itemView = outfitRecyclerView.getChildAt(position);
        Outfit outfit = outfitAdapter.getItem(position);
        if (outfit != null) {
            Log.i("SC", "ItemView not null");
            PopupMenu outfitPopupMenu = new PopupMenu(requireContext(), itemView);
            outfitPopupMenu.inflate(R.menu.outfit_options);
            outfitPopupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    Log.i("SC", "edit");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("outfit", outfit);
                    navigateToEditOutfitFragment(bundle);
                } else if (item.getItemId() == R.id.delete) {
                    Log.i("SC", "delete");
                    clothingDbViewModel.deleteOutfit(outfit);
                } else {
                    Log.i("SC", "x");

                }
                return false;
            });
            outfitPopupMenu.show();
        } else {
            Log.i("SC", "itemView null");
        }
    }

    private void showFiltersPopupMenu() {
        if (selectedFilters == null) {
            selectedFilters = new ArrayList<>();
            typeFilters = new ArrayList<>();
            colourFilters = new ArrayList<>();
            brandFilters = new ArrayList<>();
            tagFilters = new ArrayList<>();
        }
        PopupMenu filterPopupMenu = new PopupMenu(requireContext(), filterButton);
        filterPopupMenu.inflate(R.menu.filter_options);

        filterPopupMenu.setOnMenuItemClickListener(item -> {
            View typeView = getLayoutInflater().inflate(R.layout.filters_view, null);

            List<String> options;
            String title;
            List<String> list;

            if(item.getItemId() == R.id.clear_all) {
                typeFilters.clear();
                colourFilters.clear();
                brandFilters.clear();
                tagFilters.clear();
                viewModel.setSelectedFilters(selectedFilters);
            } else {
                if (item.getItemId() == R.id.type) {
                    Log.i("SC", "type");
                    options = Arrays.asList(getResources().getStringArray(R.array.clothing_types));
                    title = "Clothing Type";
                    list = typeFilters;
                } else if (item.getItemId() == R.id.colour) {
                    Log.i("SC", "colour");
                    options = Arrays.asList(getResources().getStringArray(R.array.colours));
                    title = "Colour";
                    list = colourFilters;
                } else if (item.getItemId() == R.id.brand) {
                    Log.i("SC", "brand");
                    options = clothingDbViewModel.getBrands();
                    title = "Brand";
                    list = brandFilters;
                } else {
                    Log.i("SC", "tags");
                    List<String> tagsLists = clothingDbViewModel.getTags(clothingItemList);
                    options = tagsLists;
                    Log.i("SC", "options: " + options);
                    title = "Tags";
                    list = tagFilters;
                }

                FilterAdapter filterAdapter = new FilterAdapter(getContext(), options, selectedFilters);

                RecyclerView typeRecyclerView = typeView.findViewById(R.id.typeRecyclerView);
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                typeRecyclerView.setLayoutManager(layoutManager);
                typeRecyclerView.setAdapter(filterAdapter);

                filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Log.i("SC", "OnItemClick Clothing Fragment");
                        String clickedItem = options.get(position);
                        if (list.contains(clickedItem)) {
                            list.remove(clickedItem);
                        } else {
                            list.add(clickedItem);
                        }

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);

                builder.setView(typeView);
                Button confirmButton = typeView.findViewById(R.id.confirmButton);
                Button cancelButton = typeView.findViewById(R.id.cancelButton);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.setSelectedFilters(selectedFilters);
                        alertDialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }

            return false;
        });

        filterPopupMenu.show();
    }

}

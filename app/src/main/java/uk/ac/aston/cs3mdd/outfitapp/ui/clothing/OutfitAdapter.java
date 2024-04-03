package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.ViewHolder> {

    private List<Outfit> mOutfitList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private ClothingDbViewModel mClothingDbViewModel;
    private OutfitAdapter.OnItemClickListener listener;
    private final boolean showCircle;


    public OutfitAdapter(Context context, List<Outfit> outfitList, ClothingDbViewModel dbViewModel, boolean showCircle) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOutfitList = outfitList;
        this.mClothingDbViewModel = dbViewModel;
        this.showCircle = showCircle;

    }

    @NonNull
    @Override
    public OutfitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_outfit, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitAdapter.ViewHolder holder, int position) {
        Outfit outfit = mOutfitList.get(position);
        Log.i("SC", "position" + position);
        holder.bind(outfit);
        if(showCircle) {
            holder.imageViewSelector.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewSelector.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.mOutfitList.size();
    }

    public void updateData(List<Outfit> outfitList) {
        mOutfitList = outfitList;
        notifyDataSetChanged();
    }

    public Outfit getItem(int position) {
        if (position >= 0 && position < mOutfitList.size()) {
            return mOutfitList.get(position);
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelected(List<Long> selectedIds, RecyclerView recyclerView) {
        for (int i = 0; i < mOutfitList.size(); i++) {
            Outfit currentItem = mOutfitList.get(i);
            OutfitAdapter.ViewHolder viewHolder = (OutfitAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            Log.i("SC", "currentItem: " + currentItem);
            if (selectedIds.contains(currentItem.getId())) {
                if (viewHolder != null) {
                    Log.i("SC", "image selected");

                    viewHolder.imageViewSelected.setVisibility(View.VISIBLE);
                } else {
                    Log.i("SC", "NULL viewholder");
                }
            } else {
                if (viewHolder != null) {
                    viewHolder.imageViewSelected.setVisibility(View.GONE);

                }
            }
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerView clothingRecyclerView;
        private ClothingAdapter clothingAdapter;
        private LiveData<List<ClothingItem>> clothingItems;
        private Observer<List<ClothingItem>> clothingItemsObserver;
        final OutfitAdapter mAdapter;
        private Button editButton;
        ImageView imageViewSelector;
        ImageView imageViewSelected;
        GridLayoutManager layoutManager;
        public ViewHolder(@NonNull View itemView, OutfitAdapter adapter) {
            super(itemView);
            clothingRecyclerView = itemView.findViewById(R.id.clothing_recycler_view);
//            clothingRecyclerView.setPadding(10, 20, 10, 20);
            clothingAdapter = new ClothingAdapter(mContext, new ArrayList<>(), mClothingDbViewModel, false, true);
            editButton = itemView.findViewById(R.id.buttonEditOutfit);
            clothingRecyclerView.setAdapter(clothingAdapter);
            int initialColumns = 2;
            layoutManager = new GridLayoutManager(itemView.getContext(), initialColumns);
            clothingRecyclerView.setLayoutManager(layoutManager);
            imageViewSelector = itemView.findViewById(R.id.imageViewSelector);
            imageViewSelected = itemView.findViewById((R.id.imageViewSelected));
            this.mAdapter = adapter;
            editButton.setOnClickListener(this);
        }


        public void bind(@NonNull Outfit outfit) {
            if (clothingItems != null && clothingItemsObserver != null) {
                clothingItems.removeObserver(clothingItemsObserver);
            }

            clothingItems = mClothingDbViewModel.getClothingItemsForOutfit(outfit.getId());
            clothingItemsObserver = new Observer<List<ClothingItem>>() {
                @Override
                public void onChanged(List<ClothingItem> clothingItems) {
                    int newColumns = (int) Math.ceil((double) clothingItems.size() / 2);
                    if (newColumns > 0) {
                        layoutManager.setSpanCount(newColumns);
                    }
                    List<ClothingItem> orderedClothingItems = orderClothing(clothingItems);
                    clothingAdapter.updateData(orderedClothingItems);
                }
            };
            clothingItems.observe((LifecycleOwner) mContext, clothingItemsObserver);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                    if (showCircle) if (mAdapter.showCircle) {
                        int visibility = imageViewSelected.getVisibility();
                        imageViewSelected.setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                    }
                } else {
                    Log.i("SC", "Listener ERROR ON CLICK POS");
                }
            } else {
                Log.i("SC", "Listener ERROR NULL");
            }
        }

        public List<ClothingItem> orderClothing(List<ClothingItem> clothingItems) {
            List<ClothingItem> topsList = new ArrayList<>();
            List<ClothingItem> bottomsList = new ArrayList<>();
            List<String> topTypes = Arrays.asList("Top", "Jumper", "Jacket", "Dress");

            for (ClothingItem item : clothingItems) {
                if (topTypes.contains(item.getType())) {
                    topsList.add(item);
                } else {
                    bottomsList.add(item);
                }
            }

            List<ClothingItem> sortedClothingItems = new ArrayList<>();
            sortedClothingItems.addAll(topsList);
            sortedClothingItems.addAll(bottomsList);
            return sortedClothingItems;
        }

    }

}


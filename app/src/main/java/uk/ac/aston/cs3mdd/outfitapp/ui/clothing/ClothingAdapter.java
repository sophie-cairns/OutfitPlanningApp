package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> mClothingList;
    private List<ClothingItem> mFilteredClothingList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private ClothingDbViewModel mClothingDbViewModel;
    private OnItemClickListener listener;
    private final boolean showCircle;
    private final boolean resize;

    public ClothingAdapter(Context context, List<ClothingItem> clothingList, ClothingDbViewModel dbViewModel, boolean showCircle, boolean resize) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mClothingList = clothingList;
        this.mFilteredClothingList = new ArrayList<>(clothingList);
        this.mClothingDbViewModel = dbViewModel;
        this.showCircle = showCircle;
        this.resize = resize;
    }

    @NonNull
    @Override
    public ClothingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_clothing_image, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingAdapter.ViewHolder holder, int position) {
        ClothingItem clothingItem = mFilteredClothingList.get(position);
        holder.bind(clothingItem);
        if(showCircle) {
            holder.imageViewSelector.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewSelector.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if(showCircle) {
                int visibility = holder.imageViewSelected.getVisibility();
                holder.imageViewSelected.setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }

            if(listener != null) {
                listener.onItemClick(position);
            }
        });
        if (resize) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = 300;
            layoutParams.height = 300;
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return this.mFilteredClothingList.size();
    }

    public void updateData(List<ClothingItem> clothingList) {
        mClothingList = clothingList;
        applyFilters(null, null, null, null);
    }



    public ClothingItem getItem(int position) {
        if (position >= 0 && position < mFilteredClothingList.size()) {
            return mFilteredClothingList.get(position);
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
        for (int i = 0; i < mClothingList.size(); i++) {
            ClothingItem currentItem = mClothingList.get(i);
            ViewHolder viewHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
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

    public void applyFilters(List<String> typeFilters, List<String> colourFilters, List<String> brandFilters, List<String> tagFilters) {
        mFilteredClothingList.clear();
        for (ClothingItem item : mClothingList) {
            if (matchesFilters(item, typeFilters, colourFilters, brandFilters, tagFilters)) {
                mFilteredClothingList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    private boolean matchesFilters(ClothingItem item, List<String> typeFilters, List<String> colourFilters, List<String> brandFilters, List<String> tagFilters) {
        if (typeFilters != null && !typeFilters.isEmpty() && !typeFilters.contains(item.getType())) {
            return false;
        }
        if (colourFilters != null && !colourFilters.isEmpty() && !colourFilters.contains(item.getColour())) {
            return false;
        }
        if (brandFilters != null && !brandFilters.isEmpty() && !brandFilters.contains(item.getBrand())) {
            return false;
        }
        if (tagFilters != null && !tagFilters.isEmpty() && !Collections.disjoint(tagFilters, item.getTags())) {
            return false;
        }
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageView imageViewSelector;
        ImageView imageViewSelected;
        final ClothingAdapter mAdapter;

        public ViewHolder(@NonNull View itemView, ClothingAdapter adapter) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageViewSelector = itemView.findViewById(R.id.imageViewSelector);
            imageViewSelected = itemView.findViewById((R.id.imageViewSelected));
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull ClothingItem clothingItem) {
            Glide.with(mContext)
                    .load(clothingItem.getImage())
                    .into(imageView);



        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }

}

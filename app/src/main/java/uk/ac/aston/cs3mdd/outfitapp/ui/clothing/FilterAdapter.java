package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private List<String> mFilters;
    private List<List<String>> mSelectedFilters;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private OnItemClickListener listener;

    public FilterAdapter(Context context, List<String> filters, List<List<String>> selectedFilters) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mFilters = filters;
        this.mSelectedFilters = selectedFilters;
    }

    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_filter_option, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.ViewHolder holder, int position) {
        String filter = mFilters.get(position);
        holder.textFilter.setText(filter);
        for (List<String> filterList : mSelectedFilters) {
            if (filterList.contains(filter)) {
                holder.imageViewSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewSelected.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            int visibility = holder.imageViewSelected.getVisibility();
            holder.imageViewSelected.setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);

            if(listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilters.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textFilter;
        ImageView imageViewSelector;
        ImageView imageViewSelected;
        final FilterAdapter mAdapter;

        public ViewHolder(@NonNull View itemView, FilterAdapter adapter) {
            super(itemView);
            textFilter = itemView.findViewById(R.id.text_filter);
            imageViewSelector = itemView.findViewById(R.id.imageViewSelector);
            imageViewSelected = itemView.findViewById((R.id.imageViewSelected));
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
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

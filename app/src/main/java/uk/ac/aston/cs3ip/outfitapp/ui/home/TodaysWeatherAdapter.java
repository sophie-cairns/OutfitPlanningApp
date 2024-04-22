package uk.ac.aston.cs3ip.outfitapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3ip.outfitapp.ui.calendar.WeatherViewModel;


public class TodaysWeatherAdapter extends RecyclerView.Adapter<TodaysWeatherAdapter.ViewHolder> {

    private List<Hourly> mWeatherList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private WeatherViewModel mWeatherViewModel;
    private OnItemClickListener listener;

    public TodaysWeatherAdapter(Context context, List<Hourly> weatherList, WeatherViewModel weatherViewModel) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mWeatherList = weatherList;
        this.mWeatherViewModel = weatherViewModel;
    }

    @NonNull
    @Override
    public TodaysWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_weather, parent, false);
        return new TodaysWeatherAdapter.ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TodaysWeatherAdapter.ViewHolder holder, int position) {
        Hourly weather = mWeatherList.get(position);
        holder.bind(weather);

        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mWeatherList.size();
    }

    public void updateData(List<Hourly> weatherList) {
        mWeatherList = weatherList;
        notifyDataSetChanged();
    }

    public Hourly getItem(int position) {
        if (position >= 0 && position < mWeatherList.size()) {
            return mWeatherList.get(position);
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(TodaysWeatherAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView timeTextView;
        private TextView temperatureTextView;
        private TextView chanceOfPrecipitationTextView;
        private ImageView iconImageView;
        final TodaysWeatherAdapter mAdapter;

        public ViewHolder(@NonNull View itemView, TodaysWeatherAdapter adapter) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.text_view_time);
            temperatureTextView = itemView.findViewById(R.id.text_view_temperature);
            chanceOfPrecipitationTextView = itemView.findViewById(R.id.text_view_chance_of_precipitation);
            iconImageView = itemView.findViewById(R.id.image_view_weather_icon);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Hourly weather) {
            timeTextView.setText(weather.getFormattedTime());
            temperatureTextView.setText(weather.getFormattedTemperature());
            chanceOfPrecipitationTextView.setText(weather.getFormattedPop());
            String iconUrl = weather.getWeatherIconUrl();
            if (iconUrl != null) {
                Glide.with(mContext)
                        .load(iconUrl)
                        .into(iconImageView);
            }
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

package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingAdapter;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingDbViewModel;
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingItem;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> mEvents;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private ClothingDbViewModel mClothingDbViewModel;
    private EventAdapter.OnItemClickListener listener;
    private boolean resize;


    public EventAdapter(Context context, List<Event> events, ClothingDbViewModel dbViewModel, boolean resize) {
        mInflater = LayoutInflater.from(context);
        this.mEvents = events;
        this.mContext = context;
        this.mClothingDbViewModel = dbViewModel;
        this.resize = resize;

    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mEvents.get(position);

        if (resize) {
            holder.verticalLinearLayout.setVisibility(View.VISIBLE);
            holder.horizontalLinearLayout.setVisibility(View.GONE);

            holder.eventName2TextView.setText(event.getEvent());
            Log.i("SC", "EventName1: " + event.getEvent());
            holder.eventLocation2TextView.setText(event.getLocation());
            holder.eventTime2TextView.setText(event.getTime());

            holder.buttonEditEvent2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        } else {
            holder.eventNameTextView.setText(event.getEvent());
            Log.i("SC", "EventName2: " + event.getEvent());
            holder.eventLocationTextView.setText(event.getLocation());
            holder.eventTimeTextView.setText(event.getTime());

            holder.buttonEditEvent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void updateData(List<Event> list) {
        try {
            Collections.sort(list, new Comparator<Event>() {
                @Override
                public int compare(Event event1, Event event2) {

                    LocalTime time1 = LocalTime.parse(event1.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime time2 = LocalTime.parse(event2.getTime(), DateTimeFormatter.ofPattern("HH:mm"));


                    return time1.compareTo(time2);
                }
            });

        } catch (Exception e) {
            Log.i("SC", "Incorrect Time format: " + e);
        }
        this.mEvents = list;
        notifyDataSetChanged();
    }

    public Event getItem(int position) {
        if (position >= 0 && position < mEvents.size()) {
            return mEvents.get(position);
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(EventAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView eventNameTextView;
        public final TextView eventLocationTextView;
        public final TextView eventTimeTextView;
        public final TextView eventName2TextView;
        public final TextView eventLocation2TextView;
        public final TextView eventTime2TextView;
        public final LinearLayout verticalLinearLayout;
        public final LinearLayout horizontalLinearLayout;
        private Button buttonEditEvent;
        private Button buttonEditEvent2;

        EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.titleTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventTimeTextView = itemView.findViewById(R.id.eventTimeTextView);
            buttonEditEvent = itemView.findViewById(R.id.buttonEditEvent);
            eventName2TextView = itemView.findViewById(R.id.title2TextView);
            eventLocation2TextView = itemView.findViewById(R.id.eventLocation2TextView);
            eventTime2TextView = itemView.findViewById(R.id.eventTime2TextView);
            buttonEditEvent2 = itemView.findViewById(R.id.buttonEdit2Event);
            verticalLinearLayout = itemView.findViewById(R.id.verticalLinearLayout);
            horizontalLinearLayout = itemView.findViewById(R.id.horizontalLinearLayout);

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

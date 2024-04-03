package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import uk.ac.aston.cs3mdd.outfitapp.ui.clothing.ClothingDbViewModel;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> mEvents;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private ClothingDbViewModel mClothingDbViewModel;

    public EventAdapter(Context context, List<Event> events, ClothingDbViewModel dbViewModel) {
        mInflater = LayoutInflater.from(context);
        this.mEvents = events;
        this.mContext = context;
        this.mClothingDbViewModel = dbViewModel;

    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mEvents.get(position);

        holder.eventNameTextView.setText(event.getEvent());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventTimeTextView.setText(event.getTime());


    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void updateData(List<Event> list) {
        Collections.sort(list, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {

                LocalTime time1 = LocalTime.parse(event1.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime time2 = LocalTime.parse(event2.getTime(), DateTimeFormatter.ofPattern("HH:mm"));


                return time1.compareTo(time2);
            }
        });


        this.mEvents = list;
        notifyDataSetChanged();
    }



    static class EventViewHolder extends RecyclerView.ViewHolder {
        public final TextView eventNameTextView;
        public final TextView eventLocationTextView;
        public final TextView eventTimeTextView;
        private Button buttonEditEvent;

        EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.titleTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventTimeTextView = itemView.findViewById(R.id.eventTimeTextView);
            buttonEditEvent = itemView.findViewById(R.id.buttonEditEvent);

        }
    }
}

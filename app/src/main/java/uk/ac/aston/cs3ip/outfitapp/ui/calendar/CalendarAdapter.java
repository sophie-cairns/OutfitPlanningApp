package uk.ac.aston.cs3ip.outfitapp.ui.calendar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import uk.ac.aston.cs3mdd.outfitapp.R;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private OnDateClickListener onDateClickListener;
    private LocalDate selectedDate;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnDateClickListener onDateClickListener, LocalDate selectedDate)
    {
        this.daysOfMonth = daysOfMonth;
        this.onDateClickListener = onDateClickListener;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.day_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onDateClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        String dayText = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayText != "") {
                    selectedDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), Integer.parseInt(dayText));
                    notifyDataSetChanged();

                    if (onDateClickListener != null) {
                        Log.i("SC", "HERE");
                        onDateClickListener.onDateClick(position, dayText);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnDateClickListener
    {
        void onDateClick(int position, String dayText);
    }
}

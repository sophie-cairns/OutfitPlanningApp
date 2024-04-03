package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnDateClickListener onDateClickListener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnDateClickListener onDateClickListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onDateClickListener = onDateClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        onDateClickListener.onDateClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
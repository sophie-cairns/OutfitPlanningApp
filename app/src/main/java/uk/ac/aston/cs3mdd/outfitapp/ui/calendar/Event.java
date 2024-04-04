package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
public class Event implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long eventId;
    public String event;
    public String location;
//    @ForeignKey(Date.class)
    public LocalDate date;
    public String time;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return date.format(formatter);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", date=" + date +
                ", event='" + event + '\'' +
                '}';
    }
}

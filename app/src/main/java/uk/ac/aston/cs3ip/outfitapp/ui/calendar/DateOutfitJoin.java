package uk.ac.aston.cs3ip.outfitapp.ui.calendar;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;


import java.time.LocalDate;

import uk.ac.aston.cs3ip.outfitapp.ui.clothing.Outfit;

@Entity(tableName = "date_outfit_join",
        primaryKeys = { "date", "outfitId" },
        foreignKeys = {
                @ForeignKey(entity = Date.class,
                        parentColumns = "date",
                        childColumns = "date"),
                @ForeignKey(entity = Outfit.class,
                        parentColumns = "outfitId",
                        childColumns = "outfitId",
                        onDelete = ForeignKey.CASCADE)
        })
public class DateOutfitJoin {
    @NonNull
    public LocalDate date;
    public long outfitId;

    public DateOutfitJoin(LocalDate date, long outfitId) {
        this.date = date;
        this.outfitId = outfitId;
    }

}
